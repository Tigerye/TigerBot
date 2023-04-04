package com.tigerobo.x.pai.biz.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

@Slf4j
public class Md5Util {
    private final static String MD5_SALT_AUTH_USER_PASSWORD = "auth-user-password";
    public static String getPassword(String password) {

        if (StringUtils.isBlank(password)){
            return null;
        }
        return DigestUtils.md5Hex(String.join("-", MD5_SALT_AUTH_USER_PASSWORD, password));
    }

    public static String getMd5(String text){
        if (StringUtils.isBlank(text)){
            return "";
        }

        return DigestUtils.md5Hex(text);
    }


    public static String getMd5ByBytes(byte[] bytes)  {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(bytes);
            //转换为16进制
            return new BigInteger(1, digest).toString(16);
        }catch (Exception ex){
            log.error("",ex);
            throw new RuntimeException("文件byte处理失败");
        }
    }

    public static String getInputStreamMd5(InputStream inputStream){
        MessageDigest digest = null;

        byte buffer[] = new byte[1024];
        int len;
        try
        {
            digest = MessageDigest.getInstance("MD5");


            while ((len = inputStream.read(buffer, 0, 1024)) != -1)
            {
                digest.update(buffer, 0, len);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
    public static void main(String[] args) {
//        System.out.println(getPassword("thepaper123"));


        System.out.println(getMd5ByBytes("abc".getBytes()));
    }
}
