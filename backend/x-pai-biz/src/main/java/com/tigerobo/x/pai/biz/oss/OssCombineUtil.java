package com.tigerobo.x.pai.biz.oss;

import com.tigerobo.x.pai.api.constants.OssConstant;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;

public class OssCombineUtil {


    public static String getAlgoletUrl(String url) {

        if (StringUtils.isBlank(url)) {
            return null;
        }
        return url.replaceFirst("http:", "https:")
                .replaceFirst(OssConstant.OSS_HOST_MID_INTERNAL, OssConstant.ALGOLET_MID)
                .replaceFirst(OssConstant.OSS_HOST_MID, OssConstant.ALGOLET_MID)
                ;
    }

    public static String getKeyByUrl(String ossUrl) {

        String decode = URLDecoder.decode(ossUrl);


        String sub = decode;
        if (sub.contains("?")) {
            int index = sub.indexOf("?");
            sub = sub.substring(0, index);
        }
        String str = ".com/";
        int index = sub.indexOf(str);

        String name = null;
        if (index > 0 ) {
            name = sub.substring(index + str.length());
        }
        return name;
    }

    public static String getNameByUrl(String url) {
        String key = getKeyByUrl(url);

        int index = key.lastIndexOf("/");
        if (index>0){
            return key.substring(index+1);
        }
        return key;
    }

    public static void main(String[] args) {
        String url = "https://x-pai.algolet.com/dads/5fcc8cbcf483bebc424b3d32fae40980.png?Expires=1640837378&OSSAccessKeyId=LTAI5t8HoYusAPr5MffHTauz&Signature=oWLio5Mkuu1702R2iqcsqkyBMlk%3D";

        String key = getNameByUrl(url);
        System.out.println(key);
    }

}
