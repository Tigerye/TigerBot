package com.tigerobo.x.pai.biz.utils;

import com.tigerobo.pai.common.util.DownloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Slf4j
public class DownloadImageUtil {


    public static String getUrlSuffix(String url){

        if (StringUtils.isBlank(url)){
            return "";
        }
        String sub = url;

        int index = sub.indexOf("?");
        if (index>10){
            sub = sub.substring(0,index);
        }
        index = sub.lastIndexOf(".");

        if (index>0){
            return sub.substring(index);
        }
        return "";
    }
    public static String getTmpImgFile(String imageUrl) {
        Validate.isTrue(StringUtils.isNotBlank(imageUrl),"资源链接为空");
        imageUrl = imageUrl.trim();

        if (imageUrl.contains(" ")){
            imageUrl = imageUrl.replaceAll(" ","%20");
        }

        String urlSuffix = getUrlSuffix(imageUrl);

        if (StringUtils.isBlank(urlSuffix)){
            urlSuffix = ".png";
        }

        if (!imageUrl.startsWith("http")) {
            imageUrl = "http://" + imageUrl;
        }
        int day = TimeUtil.getDayValue(new Date());
        String path = "/tmp/" + day + "/";
        String key = IdGenerator.getId() + urlSuffix;

        String imagTmpFile = null;
        try {
            imagTmpFile = DownloadUtil.downLoadByUrl(imageUrl, key, path);
        } catch (IOException e) {
            log.error("imageUrl:{}", imageUrl, e);
            throw new IllegalArgumentException("获取图片内容失败");
        }
        if (imagTmpFile == null || !(new File(imagTmpFile).exists())) {
            log.error("imageUrl:{},download,fail2path", imageUrl);
            throw new IllegalArgumentException("获取图片内容失败");
        }
        return imagTmpFile;
    }

}
