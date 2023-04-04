package com.tigerobo.x.pai.biz.ai.photo;

import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.photo.PhotoFixReq;
import com.tigerobo.x.pai.api.entity.Pair;
import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.ExecutorFactory;
import com.tigerobo.x.pai.biz.utils.DownloadImageUtil;
import com.tigerobo.x.pai.biz.utils.ImageUtil;
import com.tigerobo.x.pai.biz.utils.ThreadUtil;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.ai.dao.AiPhotoFixDao;
import com.tigerobo.x.pai.dal.ai.entity.AiPhotoFixPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Service
@Slf4j
public class PhotoFixAiService {

    @Autowired
    private AiPhotoFixDao aiPhotoFixDao;

    @Autowired
    private OssService ossService;
    @Autowired
    private ExecutorFactory executorFactory;

    @Value("${pai.ai.appendColor.inferUrl:http://gbox5.aigauss.com:9513/infer}")
    String appendColorInferUrl;

    @Autowired
    EnvService envService;

    public Long userReq(PhotoFixReq req) {

        Validate.isTrue(req.getUserId() != null, "用户不合法");
        Validate.isTrue(StringUtils.isNotBlank(req.getInputPhotoUrl()), "未上传图片");
        Validate.isTrue(StringUtils.isNotBlank(req.getInferUrl()), "未配置模型服务");


        AiPhotoFixPo po = new AiPhotoFixPo();
        po.setUserId(req.getUserId());
        po.setInputPhoto(req.getInputPhotoUrl());
        po.setProcessStatus(AiArtImageProcessEnum.PREPARE.getStatus());
        po.setReqId(req.getReqId());
        po.setApiKey(req.getApiKey());

        po.setAppendColor(req.isAppendColor()?1:0);
        aiPhotoFixDao.add(po);

        return req.getReqId();
    }

    public void dealWaitDealList(boolean test) {

        int size = 20;
        List<AiPhotoFixPo> prepareList = test?aiPhotoFixDao.getPrepareListTest(size):aiPhotoFixDao.getPrepareList(size);
        if (CollectionUtils.isEmpty(prepareList)) {
            return;
        }

        for (AiPhotoFixPo aiPhotoFixPo : prepareList) {
            try {
                doProcess(aiPhotoFixPo);
            }catch (IllegalArgumentException ex){
                log.error("deal:{}",aiPhotoFixPo.getId(),ex);
                updateFail(aiPhotoFixPo,ex.getMessage());
            }catch (Exception ex){
                log.error("deal:{}",aiPhotoFixPo.getId(),ex);
                updateFail(aiPhotoFixPo,"服务异常");
            }
        }

//
//        List<Future> futureList = new ArrayList<>();
//        for (AiPhotoFixPo aiPhotoFixPo : prepareList) {
//            Future<?> submit = ThreadUtil.executorService.submit(() -> process(aiPhotoFixPo));
//            futureList.add(submit);
//        }
//
//        while (true){
//            boolean hasDeal = false;
//            for (Future future : futureList) {
//                if (!future.isDone()){
//                    hasDeal = true;
//                    break;
//                }
//            }
//            if (hasDeal){
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    log.error("",e);
//                }
//            }else {
//                break;
//            }
//        }
    }

    private void process(AiPhotoFixPo po){

        try {
            doProcess(po);
        } catch (IllegalArgumentException ex) {
            log.error("dealPrepareTask,id:{}", po.getId(), ex);
            updateFail(po, ex.getMessage());
        } catch (Exception ex) {
            log.error("dealPrepareTask,id:{}", po.getId(), ex);
            updateFail(po, "处理异常");
        }
    }

    private void doProcess(AiPhotoFixPo po) {

        Date reqTime = new Date();
        Executable executable = executorFactory.get(po.getApiKey());

        Validate.isTrue(executable != null, "没有配置模型服务");
        String apiUri = executable.getApiUri();

        boolean appendColor = po.getAppendColor()==1;


        PhotoFix outImage = getOutImage(po,apiUri, po.getInputPhoto(), po.getId(), po.getUserId(),appendColor);
        Validate.isTrue(outImage!=null&&StringUtils.isNotBlank(outImage.image), "图片修复错误");


        AiPhotoFixPo updateSuccessPo = new AiPhotoFixPo();
        updateSuccessPo.setId(po.getId());
        updateSuccessPo.setOutputPhoto(outImage.image);
        updateSuccessPo.setCompressOutputPhoto(outImage.compressImage);

        updateSuccessPo.setReqTime(reqTime);
        updateSuccessPo.setFinishTime(new Date());
        updateSuccessPo.setProcessStatus(AiArtImageProcessEnum.SUCCESS.getStatus());

        aiPhotoFixDao.update(updateSuccessPo);
    }

    private PhotoFix getOutImage(AiPhotoFixPo po,String inferUrl, String inputUrl, Integer id, Integer userId,boolean appendColor) {

        String imageFilePath = null;

        Validate.isTrue(StringUtils.isNotBlank(inputUrl),"没有模型服务");
        imageFilePath = DownloadImageUtil.getTmpImgFile(inputUrl);

        initImgWidthAndHeight(po, imageFilePath);

        Integer width = po.getHeight();
        Integer height = po.getHeight();
        Validate.isTrue(width!=null&&height!=null,"获取图片信息失败");
        Validate.isTrue(width<12000&&height<12000,"图片太大，尺寸不支持");
        if (appendColor){
            String appendPath = appendColor(id, userId, imageFilePath);
            imageFilePath = appendPath;
        }

        return fixPhoto(inferUrl, id, userId, imageFilePath);
    }

    public void initImgWidthAndHeight(AiPhotoFixPo po, String imageFilePath) {
        if (po.getWidth() == null|| po.getWidth()==0|| po.getHeight()==null|| po.getHeight()==0){
            if (StringUtils.isBlank(imageFilePath)){
                imageFilePath = DownloadImageUtil.getTmpImgFile(po.getInputPhoto());
            }
            Pair<Integer, Integer> imageAround = ImageUtil.getImageAround(imageFilePath);
            Validate.isTrue(imageAround!=null,"获取图片大小失败");
            Integer width = imageAround.getV1();
            Integer height = imageAround.getV2();
            AiPhotoFixPo updateNum = new AiPhotoFixPo();
            updateNum.setId(po.getId());
            updateNum.setWidth(width);
            updateNum.setHeight(height);
            aiPhotoFixDao.update(updateNum);

            po.setWidth(width);
            po.setHeight(height);
        }
    }


    public String appendColor(Integer id, Integer userId, String imageFilePath) {

        int dayValue = TimeUtil.getDayValue(new Date());
        String outImgName = "color_"+id + ".png";
        String outputImg = "/tmp/photo/fix/" + dayValue + "/" + outImgName;

        File outFile = new File(outputImg);
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }

        try (OutputStream outBinStream = new FileOutputStream(outFile)) {

            long start = System.currentTimeMillis();
            String inferUrl = appendColorInferUrl;
            postWithFile(inferUrl, new File(imageFilePath), "image", outBinStream);
            log.info("photoFix-appendColor-model-time:id:{},delta:{}", id,System.currentTimeMillis()-start);
        } catch (Exception ex) {
            Validate.isTrue(outFile.exists() && outFile.length() > 100, "模型着色异常");
            log.error("id:{},userId:{}", id, userId, ex);
        }

        Validate.isTrue(outFile.exists() && outFile.length() > 100, "图片着色失败");

        return outputImg;
    }



    private PhotoFix fixPhoto(String inferUrl, Integer id, Integer userId, String imageFilePath) {
        int dayValue = TimeUtil.getDayValue(new Date());
        String outImgName = id + ".png";
        String outputImg = "/tmp/photo/fix/" + dayValue + "/" + outImgName;

        File outFile = new File(outputImg);
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }

        try (OutputStream outBinStream = new FileOutputStream(outFile)) {

            long start = System.currentTimeMillis();
            postWithFile(inferUrl, new File(imageFilePath), "image", outBinStream);
            log.info("photoFix-model-time:id:{},delta:{}", id,System.currentTimeMillis()-start);
        } catch (Exception ex) {
            Validate.isTrue(outFile.exists() && outFile.length() > 100, "图片处理异常");
            log.error("id:{},userId:{}", id, userId, ex);
        }

        Validate.isTrue(outFile.exists() && outFile.length() > 100, "图片生成失败");

//        String compressName = "/tmp/photo/fix/" + dayValue + "/"+id+"_cpr";
        String compress = compress(outputImg,null);


        String suffix = envService.isProd()?"":"t";
        String key = "model/photo_fix/"+dayValue+"/"+suffix + userId+"_" + outImgName;

        String oriOutputImgOss = ossService.uploadFile(outFile, key);

        PhotoFix fix = new PhotoFix();
        fix.compressImage = compress;
        fix.image = oriOutputImgOss;
        return fix;
    }

    public String compress(String imagePath,String outPath) {

        File file = new File(imagePath);
        Validate.isTrue(file.exists(),"生成图片失败,无法压缩");
//        String compress = ImageUtil.compress(file, outPath);

        String compress = null;
        try {
            compress = ImageUtil.png2jpg(imagePath, outPath);
        } catch (IOException e) {
            log.error("imagePath:{},png转jpg失败",imagePath,e);
            throw new IllegalArgumentException("图片类型转换失败");
        }

        if (StringUtils.isBlank(compress)){
            return null;
        }

        File compressFile = new File(compress);
        if (!compressFile.exists()||compress.length()<10){
            return null;
        }
        int dayValue = TimeUtil.getDayValue(new Date());
        String suffix = envService.isProd()?"":"t";
        String key = "model/photo_fix/"+dayValue+"/"+suffix+compressFile.getName();
        return ossService.uploadFile(compressFile, key);
    }

    public static boolean postWithFile(String url, File fileUpload, String fileParamName, OutputStream outputStream) {

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);


            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
//            MultipartEntity multipartEntity = new MultipartEntity();
            FileBody fileBody = new FileBody(fileUpload);

            entityBuilder.addPart(fileParamName, fileBody);
            post.setEntity(entityBuilder.build());
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(3 * 60 * 1000)
                    .setConnectTimeout(5 * 1000)
                    .setConnectionRequestTimeout(3 * 60 * 1000)
                    .build();

            post.setConfig(requestConfig);

            HttpResponse response = httpClient.execute(post);

            HttpEntity httpEntity = response.getEntity();

            StatusLine statusLine = response.getStatusLine();
            InputStream is = httpEntity.getContent();

            int status = statusLine.getStatusCode();
            if (status != 200) {
                return false;
            }

            IOUtils.copy(is, outputStream);

            IOUtils.closeQuietly(is);
            //response.getEntity().writeTo(outputStream);
            outputStream.flush();
            outputStream.close();

            return true;
        } catch (Exception e) {
            log.error("request error", e);
            return false;
        }
    }


    private void updateFail(AiPhotoFixPo loadPo, String msg) {

        AiPhotoFixPo po = new AiPhotoFixPo();
        po.setId(loadPo.getId());
        po.setProcessStatus(AiArtImageProcessEnum.FAIL.getStatus());
        po.setMsg(msg);

        aiPhotoFixDao.update(po);

        loadPo.setMsg(msg);
//        notifyUser(loadPo, NotifyMessageTypeEnum.ART_IMAGE_FAIL);
    }



    private class PhotoFix{
        String image;
        String compressImage;
    }
}
