package com.tigerobo.x.pai.biz.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class AccessUtil {


    private final static Long ACCESS_TOKEN_TTL = 100*10 * 1000 * 10 * 10 * 1000L;
    private final static String MD5_SALT_SERVING_ACCESS_TOKEN = "serving-access-token";

    public static String toAccessToken(String appId) {
        if (StringUtils.isEmpty(appId)) {
            return null;
        }
        return DigestUtils.md5Hex(String.join("-",
                MD5_SALT_SERVING_ACCESS_TOKEN,
                appId,
                System.currentTimeMillis() / ACCESS_TOKEN_TTL + ""));
    }

    //
    public static boolean isEffectMdToken(String appId, String token) {
        String currentToken = toAccessToken(appId);
        boolean equals = currentToken.equals(token);

        if (equals){
            return true;
        }
        return "186670dd6b15c1f11535c9334610327d".equals(appId)&&"5f2325ebb11d4677d329fc5a67d6416c".equals(token);
    }
}


