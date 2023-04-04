package com.tigerobo.x.pai.biz.ai.style.transfer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.style.transfer.AiStyleTransferGenerateReq;
import com.tigerobo.x.pai.api.entity.Pair;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.biz.biz.NotifyService;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.cache.biz.MachineUtil;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.ExecutorFactory;
import com.tigerobo.x.pai.biz.utils.*;
import com.tigerobo.x.pai.dal.ai.dao.AiStyleTransferDao;
import com.tigerobo.x.pai.dal.ai.dao.AiUserInterActDao;
import com.tigerobo.x.pai.dal.ai.entity.AiStyleTransferPo;
import com.tigerobo.x.pai.dal.ai.entity.AiUserInteractPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Slf4j
@Service
public class AiStyleTransferService {

    @Autowired
    private AiStyleTransferDao aiStyleTransferDao;
    @Value("${pai.ai.styleTransfer.progress:1000}")
    Integer totalProgress;

    String workPath = "/mnt/xpai/application/style_transfer/";
    @Value("${pai.env.aml.prefix}")
    String prefix;

    @Value("${pai.ai.styleTransfer.capacity:20}")
    Integer capacity;
    @Value("${pai.ai.styleTransfer.width:400}")
    Integer reqWidth;

    @Autowired
    private MachineUtil machineUtil;

    @Autowired
    private OssService ossService;


    private final String outImage = "output.png";
    private final String statusPath = "status.json";

    @Autowired
    private ExecutorFactory executorFactory;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private AiUserInterActDao aiUserInterActDao;

    public Long userReqProduceImage(AiStyleTransferGenerateReq req, String inferUrl) {
        Validate.isTrue(req.getUserId() != null && req.getUserId() > 0, "用户未登录");

        Validate.isTrue(StringUtils.isNotBlank(req.getContentImage()), "图片为空");
        Validate.isTrue(StringUtils.isNotBlank(req.getStyleImage()), "未选择风格迁移样式");
        Validate.isTrue(StringUtils.isNotBlank(inferUrl),"模型服务未配置");

        AiStyleTransferPo po = new AiStyleTransferPo();
        Long reqId = req.getReqId();
        if (reqId == null || reqId == 0L) {
            reqId = IdGenerator.getBaseId(machineUtil.getMachineId());
        }
        po.setReqId(reqId);
        po.setUserId(req.getUserId());
        String itemWorkPath = getWorkPath(req.getReqId());

        po.setWorkPath(itemWorkPath);

        Integer currentTotalProgress = (req.getTotalProgress() == null || req.getTotalProgress() < 1000) ? totalProgress : req.getTotalProgress();
        po.setContentImage(req.getContentImage());
        po.setStyleImage(req.getStyleImage());
        po.setStyleImageId(req.getStyleImageId());

        po.setTotalProgress(currentTotalProgress);
//        po.setProcessStatus(AiArtImageProcessEnum.ON_PROCESS.getStatus());
        po.setProcessStatus(AiArtImageProcessEnum.PREPARE.getStatus());

        po.setReqTime(new Date());
        po.setApiKey(req.getApiKey());
        po.setWidth(req.getWidth());
        po.setHeight(req.getHeight());
        aiStyleTransferDao.add(po);

        Validate.isTrue(po.getId()!=null,"新增失败");

        AiUserInteractPo aiUserInteractPo = new AiUserInteractPo();
        aiUserInteractPo.setBizType(BusinessEnum.STYLE_TRANSFER.getType());
        aiUserInteractPo.setBizId(String.valueOf(po.getId()));
        aiUserInteractPo.setUserId(po.getUserId());
        aiUserInteractPo.setTitle(po.getTitle());
        aiUserInterActDao.add(aiUserInteractPo);

        return reqId;
    }

    @Transactional(value = "paiTm")
    public void taskReqProduceImage(AiStyleTransferPo reqPo) {
        String apiKey = reqPo.getApiKey();
        Executable executable = executorFactory.get(apiKey);

        String apiUri = executable.getApiUri();
        Validate.isTrue(StringUtils.isNotBlank(apiUri), "模型服务不存在");
        String contentImagePath = DownloadImageUtil.getTmpImgFile(reqPo.getContentImage());
        Pair<Integer, Integer> imageAround = ImageUtil.getImageAround(contentImagePath);
        if (imageAround != null) {
            Integer width = imageAround.getV1();
            Integer height = imageAround.getV2();
            Validate.isTrue(width<10000&&height<10000,"图片太大，尺寸不支持");
        }
        String styleImagePath = DownloadImageUtil.getTmpImgFile(reqPo.getStyleImage());

        String itemWorkPath = getWorkPath(reqPo.getReqId());


        Integer currentTotalProgress = reqPo.getTotalProgress();
        if (currentTotalProgress == null || currentTotalProgress < 100) {
            currentTotalProgress = totalProgress;
        }
        AiStyleTransferPo po = new AiStyleTransferPo();
        po.setWorkPath(itemWorkPath);
        po.setProgress(0);
        po.setTotalProgress(currentTotalProgress);
        po.setProcessStatus(AiArtImageProcessEnum.ON_PROCESS.getStatus());
        po.setReqTime(new Date());
        po.setId(reqPo.getId());
//        po.setWidth(reqPo.getWidth());
//        po.setHeight(reqPo.getHeight());
        aiStyleTransferDao.update(po);

        reqImage(apiUri, contentImagePath,styleImagePath, itemWorkPath , currentTotalProgress);
    }

    private String getWorkPath(Long reqId) {
        int day = TimeUtil.getDayValue(new Date());

        String itemWorkPath = workPath;
        if (!StringUtils.isBlank(prefix)) {
            itemWorkPath += prefix + "/";
        }
        itemWorkPath += day + "/" + reqId + "/";
        return itemWorkPath;
    }

    private void reqImage(String inferUrl, String contentImage,String styleImage,
                          String outputPath, Integer totalProgress) {

        NasFileUtil.delDir(outputPath);
        NasFileUtil.chmodDir(outputPath);
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();


        FileSystemResource contentImageResource = new FileSystemResource(contentImage);
        map.add("content_image", contentImageResource);

        FileSystemResource styleImageResource = new FileSystemResource(styleImage);
        map.add("style_image", styleImageResource);
        map.add("output_path", outputPath);
        map.add("preserve_color",0);
        map.add("iterations", totalProgress);
        map.add("width",reqWidth);

        String s = RestUtil.postWithFile(inferUrl, map);

        if (StringUtils.isBlank(s)) {
            log.error("req:{}", JSON.toJSONString(map));
            throw new IllegalArgumentException("创建任务失败");
        }
        JSONObject jsonObject = JSON.parseObject(s);
        Integer status = jsonObject.getInteger("status");
        if (status == null || status != 0) {
            log.error("result：{}", s);
            String msg = jsonObject.getString("msg");
            if (StringUtils.isBlank(msg)) {
                msg = "模型调用失败";
            }
            throw new IllegalArgumentException(msg);
        }
    }


    public void dealPrepareTask() {
        int dealingCount = aiStyleTransferDao.countProcessStatusList(AiArtImageProcessEnum.ON_PROCESS.getStatus());
        if (dealingCount>=capacity){
            return;
        }
        int pageSize = capacity - dealingCount;
        List<AiStyleTransferPo> waitDealList = aiStyleTransferDao.getDealList(AiArtImageProcessEnum.PREPARE.getStatus(),pageSize);
        if (CollectionUtils.isEmpty(waitDealList)) {
            return;
        }
        for (AiStyleTransferPo po : waitDealList) {
            try {
                taskReqProduceImage(po);
            } catch (IllegalArgumentException ex) {
                log.error("dealPrepareTask,id:{}", po.getId(), ex);
                updateFail(po, ex.getMessage());
            } catch (Exception ex) {
                log.error("dealPrepareTask,id:{}", po.getId(), ex);
                updateFail(po, "处理异常");
            }
        }
    }

    public void dealTask() {
        int size = 100;
        List<AiStyleTransferPo> waitDealList = aiStyleTransferDao.getDealList(AiArtImageProcessEnum.ON_PROCESS.getStatus(),size);
        if (CollectionUtils.isEmpty(waitDealList)) {
            return;
        }
        for (AiStyleTransferPo po : waitDealList) {
            try {
                updateImageResult(po);
            } catch (IllegalArgumentException ex) {
                log.error("id:{}", po.getId(), ex);
                updateFail(po, ex.getMessage());
            } catch (Exception ex) {
                log.error("id:{}", po.getId(), ex);
                updateFail(po, "处理异常");
            }
        }
    }

    public void updateImageResult(AiStyleTransferPo po) throws Exception {

        Integer id = po.getId();
        String workPath = po.getWorkPath();

        if (StringUtils.isBlank(workPath)) {
            log.error("updateImageResult,id:{},workPath为空", po.getId());
            throw new IllegalArgumentException(id + ",workpath为空");
        }

        String itemStatusJsonPath = workPath + statusPath;

        File statusJsonFile = new File(itemStatusJsonPath);

        if (!statusJsonFile.exists() || statusJsonFile.length() == 0) {
            if (po.getReqTime() == null || DateUtils.addHours(po.getReqTime(), 5).before(new Date())) {
                throw new IllegalArgumentException(id + ",模型未处理");
            }
            return;
        }

        String json = FileReadUtil.readOneLineContent(itemStatusJsonPath);

        JSONObject statusJson = JSON.parseObject(json);

        Integer iter = statusJson.getInteger("iter");


        if (iter.equals(po.getProgress()) && iter < po.getTotalProgress()) {
            return;
        }

        String outImgUrl = uploadImg(po);
        if (StringUtils.isBlank(outImgUrl)) {
            return;
        }

        AiStyleTransferPo update = new AiStyleTransferPo();

        update.setId(po.getId());
        update.setOutputImage(outImgUrl);
        update.setProgress(iter);
        update.setDealTime(new Date());

        boolean updateSuccess = iter>=po.getTotalProgress();
        if (updateSuccess) {
            update.setProcessStatus(AiArtImageProcessEnum.SUCCESS.getStatus());
            update.setImageProgress(iter);
            Map<String,String> progressImages = initProgressImages(po);
            if (!CollectionUtils.isEmpty(progressImages)){
                update.setProgressImages(JSON.toJSONString(progressImages));
            }
        }
        aiStyleTransferDao.update(update);
        if (updateSuccess) {
//            notifyUser(po, NotifyMessageTypeEnum.ART_IMAGE_SUCCESS);
        }

    }

    private Map<String,String> initProgressImages(AiStyleTransferPo po) {

        String workPath = po.getWorkPath();

        List<Integer> progressList = new ArrayList<>();


        int step = 50;

        int remainSize = 5;
        int max = po.getTotalProgress();
        int delta = Math.max(1, max / step / remainSize);

        for (int i = 0; i <= remainSize; i++) {
            int plain = i * step * delta;
            if (plain <= max) {
                progressList.add(plain);
            }
        }

        if (!progressList.contains(max)) {
            progressList.add(max);
        }

        log.info("progressList:{}",JSON.toJSONString(progressList));
        Map<String,String> ossUrlMap = new TreeMap<>();
        for (Integer progressIndex : progressList) {
            String imgPath = workPath + "output-" + progressIndex + ".png";
            File imgFile = new File(imgPath);
            String key = "model/style_transfer/"+po.getUserId()+"/" + po.getReqId() + "_" + progressIndex + ".png";

            String outImgUrl = ossService.uploadFile(imgFile,key);

            if (StringUtils.isNotBlank(outImgUrl)) {
                ossUrlMap.put(String.valueOf(progressIndex),outImgUrl);
            }
        }
        return ossUrlMap;
    }

    private String uploadImg(AiStyleTransferPo po) {
        String workPath = po.getWorkPath();
        String imgPath = workPath + outImage;
        File imgFile = new File(imgPath);

        String outImgUrl = null;
        if (imgFile.exists() && imgFile.length() > 0) {
            byte[] bytes;
            try {
                bytes = Files.readAllBytes(imgFile.toPath());
            } catch (IOException e) {
                log.error("id:{}", po.getId(), e);
                throw new IllegalArgumentException(po.getId() + "读取图片异常");
            }
            String key = "model/style_transfer/"+"" + po.getReqId() + ".png";
            outImgUrl = ossService.uploadImg(bytes, key);
            Validate.isTrue(StringUtils.isNotBlank(outImgUrl), ",图片结果上传oss失败");
        }
//        Validate.isTrue(StringUtils.isNotBlank(outImgUrl),  ",没有图片");
        return outImgUrl;
    }


    private void updateFail(AiStyleTransferPo loadPo, String msg) {

        AiStyleTransferPo po = new AiStyleTransferPo();
        po.setId(loadPo.getId());
        po.setProcessStatus(AiArtImageProcessEnum.FAIL.getStatus());
        po.setMsg(msg);

        aiStyleTransferDao.update(po);

        loadPo.setMsg(msg);
//        notifyUser(loadPo, NotifyMessageTypeEnum.ART_IMAGE_FAIL);
    }
//
//    private void notifyUser(AiStyleTransferPo po, NotifyMessageTypeEnum messageTypeEnum) {
//
//        UserNotifyPo notify = new UserNotifyPo();
//        notify.setNotifyType(NotifyTypeEnum.SYSTEM.getType());
//        notify.setMessageType(messageTypeEnum.getType());
//        notify.setBizId(String.valueOf(po.getId()));
//
//        notify.setUserId(po.getUserId());
//        String title = "";
//        String text = po.getText();
//        String messageEntity = "";
//        if (messageTypeEnum == NotifyMessageTypeEnum.ART_IMAGE_FAIL) {
//            title = "\"" + text + "\"" + "生成图片失败";
//            messageEntity = "\"" + text + "\"" + "生成图片失败,原因:" + po.getMsg();
//
//        } else if (messageTypeEnum == NotifyMessageTypeEnum.ART_IMAGE_SUCCESS) {
//            title = "生成图片成功";
//            messageEntity = "\"" + text + "\"" + "生成图片成功";
//            NotifyJumpVo jumpVo = new NotifyJumpVo();
//            jumpVo.setName("查看图片:");
//            String webDomain = envService.getWebDomain();
//            String detailUrl = webDomain + "applications/art-image/" + po.getId();
//            jumpVo.setUrl(detailUrl);
//            notify.setJump(JSON.toJSONString(Arrays.asList(jumpVo)));
//        }
//
//        notify.setTitle(title);
//        notify.setMessageEntity(messageEntity);
//
//        notifyService.addNotify(notify);
//    }
}
