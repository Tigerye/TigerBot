package com.tigerobo.x.pai.biz.biz;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.constants.OssConstant;
import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.oss.OSSHome;
import com.tigerobo.x.pai.biz.oss.OssCombineUtil;
import com.tigerobo.x.pai.biz.utils.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

@Service
@Slf4j
public class OssService {


    @Value("${xpai.oss.x-pai.bucket-name:''}")
    private String bucketName;
    private String domain = OssConstant.domainUrl;
    @Autowired
    private OSSHome ossHome;

    @Value("${pai.oss.common.path.prefix}")
    String prefix;

    @Autowired
    private EnvService envService;

    public String uploadHtml(byte[] data, String path) {
        try {
            String contentType = "text/html; charset=utf-8";
            String upload = ossHome.upload(path, data, contentType);
            if (StringUtils.isBlank(upload)){
                return null;
            }
            return getOutputUrl(upload, false);
        } catch (Exception e) {
            log.error("ossUpload Error:key:" + path, e);
        }
        return null;
    }

    public String uploadXls(byte[] data, String path) {
        try {
            String contentType = "application/x-xls; charset=utf-8";
            String upload = ossHome.upload(path, data, contentType);
            return getOutputUrl(upload, true);
        } catch (Exception e) {
            log.error("ossUpload Error:key:" + path, e);
        }
        return null;
    }
    public String uploadUrlFile(String url, String key){
        return uploadUrlFile(url,key,false);
    }
    public String uploadUrlFile(String url, String key,boolean needPublic){
        try(InputStream inputStream = new URL(url).openStream();){
            String upload =  ossHome.upload(key,inputStream,null);
            ossHome.setAclPublic(key);
            Boolean longTime = needPublic?null:true;
            return getOutputUrl(upload, longTime);
        }catch (Exception ex){
            log.error("url-{},key-{}",url,key,ex);
            return null;
        }
    }


    public String uploadFile(File file, String key){
        String outImgUrl = null;
        if (file.exists() && file.length() > 0) {
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                outImgUrl = uploadImg(bytes, key);
            } catch (IOException e) {
                log.error("key:{}",key,e);
                return null;
            }
        }
        return outImgUrl;
    }

    public String uploadImg(byte[] data, String key) {

        String path = key;
        try {
            String upload = ossHome.upload(path, data, null);
            return getOutputUrl(upload, true);
        } catch (Exception e) {
            log.error("ossImgUpload Error:key:" + key, e);
        }
        return null;
    }

    public String uploadCommon(byte[] data, String key,Boolean longTime) {

        String path = key;
        try {
            String upload = ossHome.upload(path, data, null);
            return getOutputUrl(upload, longTime);
        } catch (Exception e) {
            log.error("ossCommonUpload Error:key:{}", key, e);
        }
        return null;
    }

    public String uploadFile(byte[] bytes, String inputName) throws IOException {
        return uploadFile(bytes, inputName, true);
    }

    public String uploadFilePub(byte[] bytes, String inputName,String bizName) throws IOException {
        String url = uploadFile(bytes, inputName, false,bizName);
        String key = OssCombineUtil.getKeyByUrl(url);
        ossHome.setAclPublic(key);
        return OssConstant.domainUrl+key;
    }
    public String uploadFile(byte[] bytes, String inputName, boolean longTime) throws IOException {
        return uploadFile(bytes,inputName,longTime,null);
    }
    public String uploadFile(byte[] bytes, String inputName, Boolean longTime,String bizPath) throws IOException {
        String fileUrl;
        Preconditions.checkArgument(bytes.length > 0, "文件为空");
        String key = Md5Util.getMd5ByBytes(bytes);
        if (org.springframework.util.StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("获取文件失败");
        }
        int index = inputName.lastIndexOf(".");
        String suffix = "";
        if (index > 0 && index < inputName.length()) {
            suffix = inputName.substring(index);
        }

        DateTime now = DateTime.now();
        int year = now.year().get();
//        int month = TimeUtil.getDayValue(now.toDate());
        int day = DateTime.now().dayOfMonth().get();



        String usePrefix = prefix;

        if (StringUtils.isNotBlank(bizPath)){
            usePrefix += bizPath;
        }
        if (!usePrefix.endsWith("/")){
            usePrefix += "/";
        }
        String name = usePrefix + year + "/" + day + "/" + key + suffix;
        fileUrl = uploadCommon(bytes, name,longTime);


        log.warn("fileUrl:{}", fileUrl);
        return fileUrl;
    }

    private String getOutputUrl(String url, Boolean longTime) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        if (longTime == null){
            return url;
        }
        String key = OssCombineUtil.getKeyByUrl(url);
        String privateUrl = longTime ? ossHome.getUrl(key) : ossHome.getUrlShortTime(key);
        return OssCombineUtil.getAlgoletUrl(privateUrl);
    }


}
