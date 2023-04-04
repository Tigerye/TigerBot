package com.tigerobo.x.pai.biz.utils;



import com.tigerobo.x.pai.biz.constant.RedisConstants;

import java.text.MessageFormat;

/**
 * @author:Wsen
 * @time: 2020/4/23
 **/
public class RedisKeyUtil {

    public static String getSmsConfirmCodeRedisKey(String mobile, int codeType) {
        return MessageFormat.format(RedisConstants.SMS_CODE, mobile, codeType);
    }

    public static String getCntKey(String mobile) {
        return MessageFormat.format(RedisConstants.SMS_CNT, mobile);
    }

    public static String getPwdErrKey(Integer userId) {
        return MessageFormat.format(RedisConstants.LOGIN_PWD_COUNT, userId);
    }
}
