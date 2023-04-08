package com.tigerbot.chat.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

@Slf4j
public class Md5Util {
    public static String getMd5(String text){
        if (StringUtils.isBlank(text)){
            return "";
        }
        return DigestUtils.md5Hex(text);
    }
}
