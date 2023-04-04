package com.tigerobo.x.pai.biz.admin.cache;

import com.tigerobo.x.pai.dal.admin.entity.SsoUserPo;

public class AdminHolder {


    private static ThreadLocal<SsoUserPo> ssoUserCache = new ThreadLocal<>();

    private static ThreadLocal<String> ipHolder = new ThreadLocal<>();

    public static SsoUserPo getUser(){
        return ssoUserCache.get();
    }

    public static void setUser(SsoUserPo userPo){
        ssoUserCache.set(userPo);
    }

    public static void clear(){
        ssoUserCache.remove();
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

}
