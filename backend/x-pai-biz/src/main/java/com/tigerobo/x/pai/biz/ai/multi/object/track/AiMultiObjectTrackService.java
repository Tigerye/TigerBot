package com.tigerobo.x.pai.biz.ai.multi.object.track;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.multi.object.tract.AiMultiObjectTractGenerateReq;
import com.tigerobo.x.pai.biz.biz.NotifyService;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.cache.biz.MachineUtil;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.ExecutorFactory;
import com.tigerobo.x.pai.biz.utils.DownloadImageUtil;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.NasFileUtil;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.biz.utils.http.HttpReqFileUtil;
import com.tigerobo.x.pai.biz.utils.http.HttpReqUtil;
import com.tigerobo.x.pai.dal.ai.dao.AiMultiObjectTrackDao;
import com.tigerobo.x.pai.dal.ai.entity.AiMultiObjectTrackPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

@Slf4j
@Service
public class AiMultiObjectTrackService {

    @Autowired
    private AiMultiObjectTrackDao aiMultiObjectTrackDao;

    String workPath = "/mnt/xpai/application/multi_object_tract/";
    @Value("${pai.env.aml.prefix}")
    String prefix;

    @Autowired
    private MachineUtil machineUtil;

    @Autowired
    private OssService ossService;

    String ossPath = "model/multi_object_track/";

    private final String outFile = "output.mp4";

    @Autowired
    private ExecutorFactory executorFactory;
    String videoEncodeUrl = "http://admin-api.aigauss.com/api/ximei/video/videoUrlEncode";
    int reqTime = 30*60*1000;

//    @Autowired
//    private VideoProcessor videoProcessor;
    @Autowired
    private NotifyService notifyService;

//    @Autowired
//    private AiUserInterActDao aiUserInterActDao;

    public Long userReqProduceImage(AiMultiObjectTractGenerateReq req, String inferUrl) {
        Validate.isTrue(req.getUserId() != null && req.getUserId() > 0, "用户未登录");

        Validate.isTrue(StringUtils.isNotBlank(req.getInputVideo()), "视频为空");
        Validate.isTrue(StringUtils.isNotBlank(inferUrl),"模型服务未配置");

        AiMultiObjectTrackPo po = new AiMultiObjectTrackPo();
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
        aiMultiObjectTrackDao.add(po);

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
    public void taskReqProduce(AiMultiObjectTrackPo reqPo) {
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

    private void reqDeal(AiMultiObjectTrackPo po, String inferUrl, String videoPath, String outputPath) {

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


    private String aiHttpReq(AiMultiObjectTrackPo po, String inferUrl, String videoPath, String outputPath){

        Map<String,Object> params = new LinkedHashMap<>();
        params.put("output_path",outputPath);
        final File file = new File(videoPath);
        FileSystemResource contentImageResource = new FileSystemResource(videoPath);
        try(final InputStream inputStream = contentImageResource.getInputStream()
        ){
            return HttpReqFileUtil.reqPostWithFile(inferUrl,"video",file.getName(),
                    inputStream,params,null,reqTime);
        }catch (Exception ex){
            log.error("id:{},path-{}",po.getId(),po.getInputVideo(),ex);
        }
        return null;


    }

    public void dealPrepareTask(boolean test) {

        int pageSize = 10;
        List<AiMultiObjectTrackPo> waitDealList = test? aiMultiObjectTrackDao.getDealListTest(): aiMultiObjectTrackDao.getDealList(AiArtImageProcessEnum.PREPARE.getStatus(),pageSize);
        if (CollectionUtils.isEmpty(waitDealList)) {
            return;
        }
        for (AiMultiObjectTrackPo po : waitDealList) {
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

    public void updateOutputResult(AiMultiObjectTrackPo po) {

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

        AiMultiObjectTrackPo update = new AiMultiObjectTrackPo();

        update.setId(po.getId());
        update.setWorkPath(po.getWorkPath());
        update.setReqTime(po.getReqTime());
        update.setOutput(outImgUrl);
        update.setDealTime(new Date());
        update.setProcessStatus(AiArtImageProcessEnum.SUCCESS.getStatus());

        aiMultiObjectTrackDao.update(update);

    }

    private String uploadResultOss(AiMultiObjectTrackPo po) {
        String workPath = po.getWorkPath();
        String aiOutPath = workPath + outFile;
        String aiKey = ossPath+"ai/" + po.getReqId() + ".mp4";
        String aiOutUrl = uploadOss(po, new File(aiOutPath),aiKey);
        Validate.isTrue(StringUtils.isNotBlank(aiOutUrl),"上传云存储失败");
        final String encodeUrl = encodeVideo(po, aiOutUrl);

        Validate.isTrue(StringUtils.isNotBlank(encodeUrl),"视频编码失败");
        String outKey = ossPath + po.getReqId() + ".mp4";
        String outUrl =  ossService.uploadUrlFile(encodeUrl, outKey);

        Validate.isTrue(StringUtils.isNotBlank(outUrl),  "没有结果");
        return outUrl;
    }

    private String uploadOss(AiMultiObjectTrackPo po, File outFile,String key) {
        String outUrl = null;
        if (outFile.exists() && outFile.length() > 0) {
            byte[] bytes;
            try {
                bytes = Files.readAllBytes(outFile.toPath());
            } catch (IOException e) {
                log.error("id:{}", po.getId(), e);
                throw new IllegalArgumentException(po.getId() + "读取图片异常");
            }

            outUrl = ossService.uploadImg(bytes, key);
            Validate.isTrue(StringUtils.isNotBlank(outUrl), ",图片结果上传oss失败");
        }
        return outUrl;
    }

    private String encodeVideo(AiMultiObjectTrackPo po, String aiOss) {


        Map<String,Object> map = new HashMap<>();
        map.put("url",aiOss);
        return HttpReqUtil.reqPost(videoEncodeUrl,JSON.toJSONString(map),5*60*1000,"AiMultiObjectTrack-id="+po.getId());
    }


//
//    private String encodeVideo(AiMultiObjectTrackPo po,String input){
//        final File inputFile = new File(input);
//        Validate.isTrue(inputFile.exists()&&inputFile.length()>100,"生成结果失败");
//
//        String output = workPath + po.getId()+".mp4";
//        try {
//            videoProcessor.fileEncode(input,output);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("视频编码失败");
//        }
//        return output;
//    }


    private void updateFail(AiMultiObjectTrackPo loadPo, String msg) {

        AiMultiObjectTrackPo po = new AiMultiObjectTrackPo();
        po.setId(loadPo.getId());
        po.setProcessStatus(AiArtImageProcessEnum.FAIL.getStatus());
        po.setMsg(msg);

        aiMultiObjectTrackDao.update(po);

        loadPo.setMsg(msg);
    }

}
