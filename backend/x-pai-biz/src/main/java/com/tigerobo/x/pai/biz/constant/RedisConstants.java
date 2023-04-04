package com.tigerobo.x.pai.biz.constant;

/**
 * @author:Wsen
 * @time: 2020/4/21
 **/
public class RedisConstants {
    public static final String SMS_SPEED = "uc:sms:speed:{0}";
    public static final String SMS_CNT = "uc:sms:cnt:{0}";
    public static final String SMS_CODE = "uc:sms:code:{0}-{1}";//mobile,codeType

    public static final String LOGIN_PWD_COUNT = "uc:pwd:err:{0}";//userId,密码失败次数

    public static final String UC_UID_TOKEN = "uc:user:token:%s";//product,platform,uid


    public static String getBlogViewKey(Integer id) {
        return "pai:view:blog:" + id;
    }


    public static String getBizViewKey(Integer bizType,String bizId) {
        return "pai:view:biz:count:"+bizType + ":"+bizId;
    }

    public static String getBlogShareKey(Integer id){
        return "pai:share:blog:" + id;
    }

    public static final String BLOG_CATEGORY_LIST = "pai:blog:category:list";

}
