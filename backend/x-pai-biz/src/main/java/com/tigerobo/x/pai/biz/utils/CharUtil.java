package com.tigerobo.x.pai.biz.utils;


import org.apache.commons.lang3.StringUtils;

public class CharUtil {

    public static boolean isChinese(String strName) {
        if (strName==null||strName.isEmpty()){
            return false;
        }
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
//                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) { //空白格式化字符,忽略
            return true;
        }
        return false;
    }


    public static String f2j(String text){
        if (StringUtils.isBlank(text)){
            return text;
        }
        return JianFan.f2j(text);
    }


    public static void main(String[] args) {


        System.out.println(isChinese("們"));
    }
}
