package com.tigerobo.x.pai.biz.ai.art.image;

import com.algolet.pay.api.dto.AlgCoinAddDto;
import com.algolet.pay.api.enums.AlgCoinBusinessType;
import com.algolet.pay.biz.service.AlgCoinService;
import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.enums.ArtImageType;
import com.tigerobo.x.pai.api.enums.NotifyMessageTypeEnum;
import com.tigerobo.x.pai.api.enums.NotifyTypeEnum;
import com.tigerobo.x.pai.api.vo.notify.NotifyJumpVo;
import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.biz.NotifyService;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserNotifyPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Slf4j
@Service
public class ArtImageResultService {

    @Autowired
    private AiArtImageDao aiArtImageDao;
    @Value("${pai.ai.artImage.progress:100}")
    Integer totalProgress;
    @Value("${pai.env.aml.prefix}")
    String prefix;

    @Autowired
    private OssService ossService;

    private String discoRelPath = "partials/";

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private EnvService envService;

    @Autowired
    private AlgCoinService algCoinService;

    public void updateResult(AiArtImagePo po) {
        Integer id = po.getId();
        String workPath = po.getWorkPath();

        if (StringUtils.isBlank(workPath)) {
            log.error("updateImageResult,id:{},workPath为空", po.getId());
            throw new IllegalArgumentException(id + ",workPath为空");
        }
        final Integer styleType = po.getStyleType();
        boolean updateSuccess = false;

        if (ArtImageType.DISCO.getType().equals(styleType)) {
            updateSuccess = updateDisco(po);
        } else if (ArtImageType.STABLE.getType().equals(styleType)) {
            updateSuccess = updateStable(po);
        }else {
            throw new IllegalArgumentException("styleType不支持");
        }
        if (updateSuccess){
            notifyUser(po, NotifyMessageTypeEnum.ART_IMAGE_SUCCESS);
            return;
        }
        checkModelNotDeal(po);
    }

    private boolean updateStable(AiArtImagePo po) {

        Integer id = po.getId();
        String workPath = po.getWorkPath();

        if (StringUtils.isBlank(workPath)) {
            log.error("updateImageResult,id:{},workPath为空", po.getId());
            throw new IllegalArgumentException(id + ",workPath为空");
        }

        String stableFileName = workPath + "stable.png";
        File file = new File(stableFileName);
        if (!fileCanUse(file)) {
            return false;
        }

        String outImgUrl = uploadImg(po, file);
        if (StringUtils.isBlank(outImgUrl)) {
            return false;
        }

        final List<String> iterImages = getStableIterImages(po.getReqId(), workPath, po.getNIter());

        final String stableGridImage = getStableGridImage(po.getReqId(), workPath);
        final AiArtImagePo updatePo = getStableUpdatePo(po, outImgUrl,iterImages,stableGridImage);


        aiArtImageDao.update(updatePo);
        return true;
    }


    private List<String> getStableIterImages(Long reqId, String workPath, Integer nIter){

        if (nIter == null){
            return new ArrayList<>();
        }

        List<String> list = new ArrayList<>();

        for (int i = 0; i < nIter*3; i++) {
            String imagePath = workPath+"samples/" + i +".png";

            File file = new File(imagePath);
            String imgUrl = "";
            if (file.exists()&&file.length()>0){
                imgUrl = uploadStableProcessImg(reqId, i, file);
            }
            if (imgUrl == null){
                imgUrl = "";
            }
            list.add(imgUrl);
        }
        return list;

    }

    private String getStableGridImage(Long reqId, String workPath){


        String imagePath = workPath+"grid.png";

        File file = new File(imagePath);
        String imgUrl = "";
        if (file.exists()&&file.length()>0){
            imgUrl = uploadStableGridImg(reqId,  file);
        }
        if (imgUrl == null){
            imgUrl = "";
        }

        log.info("stable-grid:img-url:{}",imgUrl);
        return imgUrl;
    }


    private AiArtImagePo getStableUpdatePo(AiArtImagePo po, String outImgUrl,
                                           List<String> iterImages, String stableGridImage) {
        Integer maxRate = 100;
        AiArtImagePo update = new AiArtImagePo();

        update.setId(po.getId());
        update.setOutputImage(outImgUrl);
        update.setProgress(maxRate);
        update.setDealTime(new Date());

        if (iterImages.size()>0){
            update.setIterImages(JSON.toJSONString(iterImages));
        }
        Map<String, String> progressImages = new HashMap<>();
        progressImages.put(maxRate.toString(), outImgUrl);
        update.setProgressImages(JSON.toJSONString(progressImages));
        update.setImageProgress(maxRate);



        update.setGridImage(stableGridImage);
        update.setProcessStatus(AiArtImageProcessEnum.SUCCESS.getStatus());
        return update;

    }

    private boolean updateDisco(AiArtImagePo po) {

        String workPath = po.getWorkPath();

        String resultDir = workPath + discoRelPath;

        File resultDirFile = new File(resultDir);

        TreeMap<Integer, File> rateFileMap = getRatioMap(resultDirFile);

        if (rateFileMap.isEmpty()) {
            return false;
        }

        final Integer maxRate = new ArrayList<>(rateFileMap.keySet()).stream().sorted().max(Integer::compareTo).get();

        final File file = rateFileMap.get(maxRate);
        String outImgUrl = uploadImg(po, file);
        if (StringUtils.isBlank(outImgUrl)) {
            return false;
        }

        AiArtImagePo update = getDiscoUpdatePo(po, rateFileMap, maxRate, outImgUrl);
        
        boolean needUpdate = update.getProcessStatus()!=null || (!update.getProgress().equals(po.getProgress()));
        if (needUpdate) {
            aiArtImageDao.update(update);
        }

        return AiArtImageProcessEnum.SUCCESS.getStatus().equals(update.getProcessStatus());
    }

    private AiArtImagePo getDiscoUpdatePo(AiArtImagePo po, TreeMap<Integer, File> rateFileMap, Integer maxRate, String outImgUrl) {
        AiArtImagePo update = new AiArtImagePo();

        update.setId(po.getId());
        update.setOutputImage(outImgUrl);
        update.setProgress(maxRate);
        update.setDealTime(new Date());

        boolean updateSuccess = maxRate == 100;
        if (updateSuccess) {
            update.setProcessStatus(AiArtImageProcessEnum.SUCCESS.getStatus());
            update.setImageProgress(maxRate);
            Map<String, String> progressImages = initProgressImagesV2(po, rateFileMap);
            if (!CollectionUtils.isEmpty(progressImages)) {
                update.setProgressImages(JSON.toJSONString(progressImages));
            }
        } else if (nearSuccess(po)) {
            update.setProcessStatus(AiArtImageProcessEnum.SUCCESS.getStatus());
            update.setImageProgress(80);
            update.setTotalProgress(80);
            Map<String, String> progressImages = initProgressImagesV2(po, rateFileMap);
            if (!CollectionUtils.isEmpty(progressImages)) {
                update.setProgressImages(JSON.toJSONString(progressImages));
            }
        }
        return update;
    }

    private TreeMap<Integer, File> getRatioMap(File resultDirFile) {
        TreeMap<Integer, File> rateFileMap = new TreeMap<>();


        final List<String> periodList = Arrays.asList("00", "20", "40", "60", "80");

        for (File file : resultDirFile.listFiles()) {

            if (!fileCanUse(file)) {
                continue;
            }
            final String name = file.getName();
            if (!name.toLowerCase().endsWith(".png") || !name.startsWith("TimeToDisco")) {
                continue;
            }
            Integer rate = null;

            if (name.equalsIgnoreCase("TimeToDisco.png")) {
                rate = 100;
            } else {
                for (String s : periodList) {
                    if (name.contains(s + "%")) {
                        rate = Integer.parseInt(s);
                    }
                }
            }
            if (rate == null) {
                continue;
            }
            rateFileMap.put(rate, file);
        }
        return rateFileMap;
    }

    private boolean nearSuccess(AiArtImagePo dbPo) {

        final Integer progress = dbPo.getProgress();
        if (progress != 80) {
            return false;
        }
        final Date reqTime = dbPo.getReqTime();
        if (reqTime == null) {
            return false;
        }
        return reqTime.getTime() + 30 * 60_1000 < System.currentTimeMillis();

    }

    private Map<String, String> initProgressImagesV2(AiArtImagePo po, Map<Integer, File> rateFileMap) {

//        String workPath = po.getWorkPath();

        Map<String, String> ossUrlMap = new TreeMap<>();
        if (rateFileMap == null) {
            return ossUrlMap;
        }

        int i = 0;
        for (Map.Entry<Integer, File> entry : rateFileMap.entrySet()) {

            int index = i++;
            final File imgFile = entry.getValue();
            String key = "model/art_image/" + po.getUserId() + "/" + po.getReqId() + "_" + index + ".png";
            String outImgUrl = ossService.uploadFile(imgFile, key);

            if (StringUtils.isNotBlank(outImgUrl)) {
                ossUrlMap.put(String.valueOf(entry.getKey()), outImgUrl);
            }
        }

        return ossUrlMap;
    }


    boolean fileCanUse(File imgFile) {
        if (!imgFile.exists()) {
            return false;
        }
        final long lastModified = imgFile.lastModified();
        final long current = System.currentTimeMillis();
        if (current - lastModified < 3000) {
            return false;
        }
        return true;
    }

    private String uploadImg(AiArtImagePo po, File imgFile) {


        String outImgUrl = null;
        if (imgFile.exists() && imgFile.length() > 0) {

            byte[] bytes;
            try {
                bytes = Files.readAllBytes(imgFile.toPath());
            } catch (IOException e) {
                log.error("id:{}", po.getId(), e);
                throw new IllegalArgumentException(po.getId() + "读取图片异常");
            }
            String key = "model/art_image/" + "" + po.getReqId() + ".png";
            outImgUrl = ossService.uploadImg(bytes, key);
            Validate.isTrue(StringUtils.isNotBlank(outImgUrl), ",图片结果上传oss失败");
        }
//        Validate.isTrue(StringUtils.isNotBlank(outImgUrl),  ",没有图片");
        return outImgUrl;
    }


    private String uploadStableProcessImg(Long reqId,int i, File imgFile) {


        String pathKey = "art_image/"+reqId +"_"+i;
        return uploadWorkRelImage(reqId, imgFile, pathKey);
    }


    private String uploadStableGridImg(Long reqId, File imgFile) {


        String pathKey = "grid/"+reqId;
        return uploadWorkRelImage(reqId, imgFile, pathKey);
    }

    private String uploadWorkRelImage(Long reqId, File imgFile, String pathKey) {
        String outImgUrl = null;
        if (imgFile.exists() && imgFile.length() > 0) {

            byte[] bytes;
            try {
                bytes = Files.readAllBytes(imgFile.toPath());
            } catch (IOException e) {
                log.error("id:{}", reqId, e);
                throw new IllegalArgumentException(pathKey + "读取图片异常");
            }
            String key = "model/"  + pathKey + ".png";
            outImgUrl = ossService.uploadImg(bytes, key);
            Validate.isTrue(StringUtils.isNotBlank(outImgUrl), ",图片结果上传oss失败");
        }
//        Validate.isTrue(StringUtils.isNotBlank(outImgUrl),  ",没有图片");
        return outImgUrl;
    }

    @Transactional(transactionManager = "paiTm")
    void updateFail(AiArtImagePo loadPo, String msg) {
        updateFail(loadPo,msg,null);
    }

    @Transactional(transactionManager = "paiTm")
    public void updateFail(AiArtImagePo loadPo, String msg,Integer adminId) {

        AiArtImagePo po = new AiArtImagePo();
        po.setId(loadPo.getId());
        po.setProcessStatus(AiArtImageProcessEnum.FAIL.getStatus());
        po.setMsg(msg);

        if (adminId!=null){
            po.setOperator(adminId.toString());
        }
        aiArtImageDao.update(po);

        loadPo.setMsg(msg);
        notifyUser(loadPo, NotifyMessageTypeEnum.ART_IMAGE_FAIL);

        final Integer coinStatus = loadPo.getCoinStatus();
        if (coinStatus!=null&&coinStatus.equals(1)&&loadPo.getCoinTotal()!=null&&loadPo.getCoinTotal()>0){

            AlgCoinAddDto addDto = AlgCoinAddDto.builder()
                    .userId(loadPo.getUserId())
                    .num(loadPo.getCoinTotal())
                    .bizType(AlgCoinBusinessType.ART_IMAGE_REFUND)
                    .refId(String.valueOf(loadPo.getId()))
                    .build();
            algCoinService.addBusiness(addDto);
        }

    }

    private void notifyUser(AiArtImagePo po, NotifyMessageTypeEnum messageTypeEnum) {

        UserNotifyPo notify = new UserNotifyPo();
        notify.setNotifyType(NotifyTypeEnum.SYSTEM.getType());
        notify.setMessageType(messageTypeEnum.getType());
        notify.setBizId(String.valueOf(po.getId()));

        notify.setUserId(po.getUserId());
        String title = "";
        String text = po.getText();
        if (text!=null&&text.length()>50){
            text = text.substring(0,50);
        }
        String messageEntity = "";
        if (messageTypeEnum == NotifyMessageTypeEnum.ART_IMAGE_FAIL) {
            title = "\"" + text + "\"" + "生成图片失败";
            messageEntity = "\"" + text + "\"" + "生成图片失败,原因:" + po.getMsg();

        } else if (messageTypeEnum == NotifyMessageTypeEnum.ART_IMAGE_SUCCESS) {
            title = "生成图片成功";
            messageEntity = "\"" + text + "\"" + "生成图片成功";
            NotifyJumpVo jumpVo = new NotifyJumpVo();
            jumpVo.setName("查看图片:");
            String webDomain = envService.getWebDomain();
            String detailUrl = webDomain + "applications/art-image/" + po.getId();
            jumpVo.setUrl(detailUrl);
            notify.setJump(JSON.toJSONString(Arrays.asList(jumpVo)));
        }

        notify.setTitle(title);
        notify.setMessageEntity(messageEntity);

        notifyService.addNotify(notify);
    }


    private void checkModelNotDeal(AiArtImagePo po) {

        final Date dealTime = po.getDealTime();
        final Date reqTime = po.getReqTime();

        Date preTime = dealTime == null?reqTime:dealTime;

        if (preTime == null){
            return;
        }
        if (DateUtils.addHours(preTime, 5).before(new Date())) {
            throw new IllegalArgumentException("模型未处理");
        }
    }

}
