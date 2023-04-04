package com.tigerobo.x.pai.biz.utils;

public class ThreadLocalHolder {

    private static ThreadLocal<Integer> user = new InheritableThreadLocal<>();

    private static ThreadLocal<String> ipHolder = new InheritableThreadLocal<>();
    private static ThreadLocal<String> tokenHolder = new InheritableThreadLocal<>();

    public static Integer getUserId(){
        return user.get();
    }

    public static void setUserId(Integer userId){
        user.set(userId);
    }

    public static void clear(){
        user.remove();
        ipHolder.remove();
    }


    public static String getIp(){
        return ipHolder.get();
    }

    public static void setIp(String ipVal){
        ipHolder.set(ipVal);
    }

    public static void clearIp(){
        ipHolder.remove();
    }


    public static String getToken(){
        return tokenHolder.get();
    }

    public static void setToken(String ipVal){
        tokenHolder.set(ipVal);
    }

    public static void clearToken(){
        tokenHolder.remove();
    }
}
