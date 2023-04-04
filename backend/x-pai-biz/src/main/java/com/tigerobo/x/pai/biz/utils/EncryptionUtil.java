package com.tigerobo.x.pai.biz.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

import static org.apache.commons.lang.StringUtils.EMPTY;


public class EncryptionUtil {

    private static final String ENCODING_KEY = "ASCII";
    private static final String ENCODING_TEXT = "UTF-8";

    /**
     * 区别AES算法对空格字符的混淆
     */
    private static final String SPACE_INSTEAD = "~$#!^";
    private static final String SPACE2_INSTEAD = "^#~$!";
    private static final String SPACE2 = String.valueOf((char) 0);
    private static final String SPACELIST = " " + SPACE2;

    /**
     * AES算法使用的KEY和IV，必须为16位或32位字符串
     */
    private static final String ENCRYPT_KEY = "tigerobo12340726";
    private static final String ENCRYPT_IV = "tigerobo12340726";

    /**
     * 加密函数
     *
     * @param text 待加密文本
     * @return 加密后的字符串或空值
     */
    public static String encrypt4NetAndJava(String text, String key, String iv) {
        // 空处理
        if (StringUtils.isBlank(text)) {
            return EMPTY;
        }
        try {
            byte[] bytes = text.getBytes(ENCODING_TEXT);
            byte[] encryptedBytes;
            encryptedBytes = encrypt(bytes, key.getBytes(ENCODING_KEY), iv.getBytes(ENCODING_KEY));
            if (!ArrayUtils.isEmpty(encryptedBytes)) {
                return parseByte2Hex(encryptedBytes);
            }
        } catch (UnsupportedEncodingException e) {
            //do nothing
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY;
    }

    /**
     * 加密函数
     *
     * @param text 待加密文本
     * @return 加密后的字符串或空值
     */
    public static String encrypt4NetAndJava(String text) {
        return encrypt4NetAndJava(text, ENCRYPT_KEY, ENCRYPT_IV);
    }

    /**
     * 解密函数
     *
     * @param cipherText 待解密文本
     * @return
     */
    public static String decrypt4NetAndJava(String cipherText, String key, String iv) {
        // 空处理
        if (StringUtils.isBlank(cipherText)) {
            return EMPTY;
        }
        try {
            byte[] cipherBytes = parseHex2Byte(cipherText);
            byte[] decryptedBytes = decrypt(cipherBytes, key.getBytes(ENCODING_KEY), iv.getBytes(ENCODING_KEY));
            if (!ArrayUtils.isEmpty(decryptedBytes)) {
                String decrypted = new String(decryptedBytes, ENCODING_TEXT);
                return StringUtils.strip(decrypted, SPACELIST)
                        .replace(SPACE_INSTEAD, " ")
                        .replace(SPACE2_INSTEAD, SPACE2);
            }
        } catch (UnsupportedEncodingException e) {
            // do nothing
        } catch (Exception e) {
            System.out.println(e);
        }
        return EMPTY;
    }

    /**
     * 解密函数
     *
     * @param cipherText 待解密文本
     * @return
     */
    public static String decrypt4NetAndJava(String cipherText) {
        return decrypt4NetAndJava(cipherText, ENCRYPT_KEY, ENCRYPT_IV);
    }

    /**
     * 使用AES算法,使用指定KEY和IV对指定字节数组进行加密
     */
    private static byte[] encrypt(byte[] bytes, byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec KeySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, KeySpec, new IvParameterSpec(iv));
            return cipher.doFinal(padWithZeros(bytes));
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * 使用AES算法，使用指定KEY和IV对指定字节数组进行解密
     */
    private static byte[] decrypt(byte[] bytes, byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec KeySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, KeySpec, new IvParameterSpec(iv));
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // 加密
    public static String encrypt(String source, String key, String iv) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = key.getBytes(ENCODING_KEY);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(source.getBytes(ENCODING_TEXT));
            return parseByte2Hex(encrypted);// 此处使用BASE64做转码。
        } catch (Exception ex) {
            return null;
        }
    }

    // 解密
    public static String decrypt(String source, String key, String iv) throws Exception {
        try {
            byte[] raw = key.getBytes(ENCODING_KEY);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
            byte[] encrypted1 = parseHex2Byte(source);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, ENCODING_TEXT);
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    private static byte[] padWithZeros(byte[] input) {
        int rest = input.length % 16;
        if (rest > 0) {
            byte[] result = new byte[input.length + (16 - rest)];
            System.arraycopy(input, 0, result, 0, input.length);
            return result;
        }
        return input;
    }

    private static byte[] parseHex2Byte(String hexText) {
        if (StringUtils.isEmpty(hexText)) {
            return null;
        }
        try {
            byte[] result = new byte[hexText.length() / 2];
            for (int i = 0; i < hexText.length() / 2; i++) {
                int high = Integer.parseInt(hexText.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexText.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte) (high * 16 + low);
            }
            return result;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static String parseByte2Hex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 返回md5加密串
     *
     * @param plainText 加密文本
     * @param needShort 是否返回16位，否则返回32位
     * @param uppercase 是否返回大写形式
     * @return String encrypted 加密字符串
     */
    public static String md5Hex(String plainText, boolean needShort, boolean uppercase) {
        String encrypted = DigestUtils.md5Hex(plainText);
        if (needShort) {
            encrypted = encrypted.substring(8, 24);
        }
        if (uppercase) {
            encrypted = encrypted.toUpperCase();
        }
        return encrypted;
    }

    public static void main(String[] args) throws Exception {


        String s = encrypt4NetAndJava("sss" + "-" + System.currentTimeMillis());
        System.out.println(s);
        s = "5c1294af611f2bf876378ace294dd7f7f25ef54e5e8646f7ef4d204627af8fe39a1ed88352ad54193d8244aea4e2cefda9464ad4ede3a7d03bae423611028b99";
        String ori = decrypt4NetAndJava(s);
        System.out.println(ori);
    }
}
