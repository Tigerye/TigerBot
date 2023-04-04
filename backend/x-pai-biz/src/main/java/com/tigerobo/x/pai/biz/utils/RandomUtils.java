package com.tigerobo.x.pai.biz.utils;

import java.util.Random;

/**
 * @author:Wsen
 * @time: 2020/4/20
 **/
public class RandomUtils {

    /**
     * 生成数字随机数
     *
     * @param length 长度
     * @return
     */
    public static int getRandomInt(int length) {
        int[] array = { 1, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Random rand = new Random();
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < length; i++) {
            result = result * 10 + array[i];
        }
        return result;
    }

    /**
     * 生成字符串随机数
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) { // length表示生成字符串的长度
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456";
//        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomNum(int length) { // length表示生成字符串的长度
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int randomString = getRandomInt(2);
        System.out.println(randomString);
    }
}
