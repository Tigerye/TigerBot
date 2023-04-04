package com.tigerobo.x.pai.biz.ai.spatio.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.spatio.action.AiSpatioActionGenerateReq;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.biz.biz.NotifyService;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.cache.biz.MachineUtil;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.ExecutorFactory;
import com.tigerobo.x.pai.biz.utils.*;
import com.tigerobo.x.pai.biz.utils.http.HttpReqFileUtil;
import com.tigerobo.x.pai.dal.ai.dao.AiSpatioActionDao;
import com.tigerobo.x.pai.dal.ai.dao.AiUserInterActDao;
import com.tigerobo.x.pai.dal.ai.entity.AiSpatioActionPo;
import com.tigerobo.x.pai.dal.ai.entity.AiUserInteractPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiSpatioActionService {

    @Autowired
    private AiSpatioActionDao aiSpatioActionDao;

    String workPath = "/mnt/xpai/application/spatio/";
    @Value("${pai.env.aml.prefix}")
    String prefix;

    @Value("${pai.ai.styleTransfer.capacity:1}")
    Integer capacity;
    @Value("${pai.ai.styleTransfer.width:400}")
    Integer reqWidth;

    @Autowired
    private MachineUtil machineUtil;

    @Autowired
    private OssService ossService;



    private final String outImage = "output.mp4";
    private final String statusPath = "status.json";

    @Autowired
    private ExecutorFactory executorFactory;

    @Autowired
    private NotifyService notifyService;

//    @Autowired
//    private AiUserInterActDao aiUserInterActDao;

    public Long userReqProduceImage(AiSpatioActionGenerateReq req, String inferUrl) {
        Validate.isTrue(req.getUserId() != null && req.getUserId() > 0, "用户未登录");

        Validate.isTrue(StringUtils.isNotBlank(req.getInputVideo()), "视频为空");
        Validate.isTrue(StringUtils.isNotBlank(inferUrl),"模型服务未配置");

        AiSpatioActionPo po = new AiSpatioActionPo();
        Long reqId = req.getReqId();
        if (reqId == null || reqId == 0L) {
            reqId = IdGenerator.getBaseId(machineUtil.getMachineId());
        }
        po.setReqId(reqId);
        po.setUserId(req.getUserId());
        String itemWorkPath = getWorkPath(req.getReqId());

        po.setWorkPath(itemWorkPath);

        po.setInputVideo(req.getInputVideo());

        //        po.setProcessStatus(AiArtImageProcessEnum.ON_PROCESS.getStatus());
        po.setProcessStatus(AiArtImageProcessEnum.PREPARE.getStatus());

        po.setReqTime(new Date());
        po.setApiKey(req.getApiKey());
        aiSpatioActionDao.add(po);

        Validate.isTrue(po.getId()!=null,"新增失败");
//
//        AiUserInteractPo aiUserInteractPo = new AiUserInteractPo();
//        aiUserInteractPo.setBizType(businessEnum.getType());
//        aiUserInteractPo.setBizId(String.valueOf(po.getId()));
//        aiUserInteractPo.setUserId(po.getUserId());
//        aiUserInteractPo.setTitle(po.getTitle());
//        aiUserInterActDao.add(aiUserInteractPo);

        return reqId;
    }

    @Transactional(value = "paiTm")
    public void taskReqProduce(AiSpatioActionPo reqPo) {
        String apiKey = reqPo.getApiKey();
        Executable executable = executorFactory.get(apiKey);

        String apiUri = executable.getApiUri();
        Validate.isTrue(StringUtils.isNotBlank(apiUri), "模型服务不存在");
        String videoPath = DownloadImageUtil.getTmpImgFile(reqPo.getInputVideo());
        String itemWorkPath = getWorkPath(reqPo.getReqId());

        reqPo.setWorkPath(itemWorkPath);
        reqPo.setReqTime(new Date());


        reqDeal(reqPo,apiUri, videoPath, itemWorkPath);
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

    private void reqDeal(AiSpatioActionPo po, String inferUrl, String videoPath, String outputPath) {

        NasFileUtil.delDir(outputPath);
        NasFileUtil.chmodDir(outputPath);
//        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
//
//
//        FileSystemResource contentImageResource = new FileSystemResource(videoPath);
//        map.add("video", contentImageResource);
//        map.add("output_path", outputPath);
//
//        String s = RestUtil.postWithFile(inferUrl, map);

        final String s = aiHttpReq(po, inferUrl, videoPath, outputPath);
        if (StringUtils.isBlank(s)) {
            log.error("req:{}", JSON.toJSONString(videoPath));
            throw new IllegalArgumentException("模型调用失败");
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

        updateOutputResult(po);
    }


    private String aiHttpReq(AiSpatioActionPo po,String inferUrl,String videoPath,String outputPath){

        Map<String,Object> params = new LinkedHashMap<>();
        params.put("output_path",outputPath);
        final File file = new File(videoPath);
        FileSystemResource contentImageResource = new FileSystemResource(videoPath);
        try(final InputStream inputStream = contentImageResource.getInputStream()
        ){
            return HttpReqFileUtil.reqPostWithFile(inferUrl,"video",file.getName(),
                    inputStream,params,null,3*60*1000);
        }catch (Exception ex){
            log.error("id:{},path-{}",po.getId(),po.getInputVideo(),ex);
        }
        return null;


    }

    public void dealPrepareTask(boolean test) {

        int pageSize = 10;
        List<AiSpatioActionPo> waitDealList = test?aiSpatioActionDao.getDealListTest():aiSpatioActionDao.getDealList(AiArtImageProcessEnum.PREPARE.getStatus(),pageSize);
        if (CollectionUtils.isEmpty(waitDealList)) {
            return;
        }
        for (AiSpatioActionPo po : waitDealList) {
            try {
                taskReqProduce(po);
            } catch (IllegalArgumentException ex) {
                log.error("dealPrepareTask,id:{}", po.getId(), ex);
                updateFail(po, ex.getMessage());
            } catch (Exception ex) {
                log.error("dealPrepareTask,id:{}", po.getId(), ex);
                updateFail(po, "处理异常");
            }
        }
    }

    public void updateOutputResult(AiSpatioActionPo po) {

        Integer id = po.getId();
        String workPath = po.getWorkPath();

        if (StringUtils.isBlank(workPath)) {
            log.error("updateResult,id:{},workPath为空", po.getId());
            throw new IllegalArgumentException(id + ",workpath为空");
        }

        String outImgUrl = uploadResultOss(po);
        if (StringUtils.isBlank(outImgUrl)) {
            throw new IllegalArgumentException("模型未生成内容");
        }

        AiSpatioActionPo update = new AiSpatioActionPo();

        update.setId(po.getId());
        update.setWorkPath(po.getWorkPath());
        update.setReqTime(po.getReqTime());
        update.setOutput(outImgUrl);
        update.setDealTime(new Date());
        update.setProcessStatus(AiArtImageProcessEnum.SUCCESS.getStatus());

        aiSpatioActionDao.update(update);

    }

    private String uploadResultOss(AiSpatioActionPo po) {
        String workPath = po.getWorkPath();
        String imgPath = workPath + outImage;
        File imgFile = new File(imgPath);

        String outUrl = null;
        if (imgFile.exists() && imgFile.length() > 0) {
            byte[] bytes;
            try {
                bytes = Files.readAllBytes(imgFile.toPath());
            } catch (IOException e) {
                log.error("id:{}", po.getId(), e);
                throw new IllegalArgumentException(po.getId() + "读取图片异常");
            }
            String key = "model/spatio_action/"+"" + po.getReqId() + ".mp4";
            outUrl = ossService.uploadImg(bytes, key);
            Validate.isTrue(StringUtils.isNotBlank(outUrl), ",图片结果上传oss失败");
        }
//        Validate.isTrue(StringUtils.isNotBlank(outImgUrl),  ",没有图片");
        return outUrl;
    }


    private void updateFail(AiSpatioActionPo loadPo, String msg) {

        AiSpatioActionPo po = new AiSpatioActionPo();
        po.setId(loadPo.getId());
        po.setProcessStatus(AiArtImageProcessEnum.FAIL.getStatus());
        po.setMsg(msg);

        aiSpatioActionDao.update(po);

        loadPo.setMsg(msg);
    }

}
