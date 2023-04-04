package com.tigerobo.x.pai.biz.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JianFan {

    private static Map<Character, Character> f2jMap;
    private static Map<Character, Character> j2fMap;

    static volatile boolean isInit = false;


    static void init(){
        if (isInit){
            return;
        }
        doInit();
    }

    synchronized static void doInit(){
        if (isInit){
            return;
        }
        List<String> jianFanList = ResourceFileUtil.readToList("jianfan/simp_trad.txt");

        f2jMap = new HashMap<>();
        j2fMap = new HashMap<>();
        for (String line : jianFanList) {
            char jian = line.charAt(0);
            char fan = line.charAt(2);
            f2jMap.put(fan, jian);
            j2fMap.put(jian, fan);
        }
        isInit = true;
    }

    public static boolean isFan(String str){
        if (str == null){
            return false;
        }
        init();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (f2jMap.containsKey(c)){
                return true;
            }
        }
        return false;
    }

    public static String f2j(String str) {
        init();
        StringBuilder sbStr = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            Character character = f2jMap.get(c);
            if(character == null){
                sbStr.append(c);
            } else {
                sbStr.append(character);
            }
        }

        return sbStr.toString();
    }

    public static String j2f(String str) {
        init();
        StringBuilder sbStr = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            Character character = j2fMap.get(c);
            if(character == null){
                sbStr.append(c);
            } else {
                sbStr.append(character);
            }
        }

        return sbStr.toString();
    }

    public static void main(String[] args) {
        String text = "中国的首都在哪?";
        System.out.println(isFan(text));
//        System.out.println(j2f(text));

        String fText = "中國的首都在哪";

        System.out.println(isFan(fText));
    }
}
