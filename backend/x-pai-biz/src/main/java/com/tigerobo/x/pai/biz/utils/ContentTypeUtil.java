package com.tigerobo.x.pai.biz.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class ContentTypeUtil {


    public static String getSuffix(String contentType,String fileName){
        String contentExtension = getContentExtension(contentType);
        if (StringUtils.isEmpty(contentExtension)){
            return contentExtension;
        }
        return getSuffixByName(fileName);
    }
    public static String getContentExtension(String contentType){

        if (StringUtils.isEmpty(contentType)){
            return "";
        }
        try {
            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            MimeType jpeg = allTypes.forName(contentType);
            return jpeg.getExtension();
        }catch (Exception ex){
            log.error("getContentExtension",ex);
        }
        return "";
    }


    public static String getSuffixByName(String name) {
        String suffix = "";
        if (StringUtils.isEmpty(name)){
            return suffix;
        }

        int index = name.lastIndexOf(".");
        if (index>0){
            suffix = name.substring(index);
        }

        if (!StringUtils.isEmpty(suffix)){

            int endIndex = suffix.indexOf("?");
            if (endIndex>0){
                return suffix.substring(0,endIndex);
            }

            return suffix;
        }

        return suffix;
    }

    public static void main(String[] args) {
        String contentExtension = getContentExtension("image/jpeg");
        System.out.println(contentExtension);

        System.out.println(getSuffixByName("a.mp4?a=1"));
    }
}
