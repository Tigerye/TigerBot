package com.tigerobo.x.pai.biz.pay.util;


import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.biz.pay.constant.SignType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
public class WXCheckUtil {


    public static String parseQrPayResp(String resContent, String nonceStr,String configMchId,String configMchSecret) throws Exception{
        Map resMap = WXPayUtil.xmlToMap(resContent);

        log.info("wechat pay return map-{}", JSON.toJSONString(resMap));

        if (!"SUCCESS".equals(resMap.get("result_code"))) {
            String return_msg = resMap.get("return_msg").toString();
            log.error("wechat pay return_msg error...,{}",return_msg);
            throw new IllegalArgumentException(return_msg);
        }

        String sign = resMap.get("sign").toString();

        String appId = resMap.get("appid").toString();
        String mchId = resMap.get("mch_id").toString();

        String prepayId = resMap.get("prepay_id").toString();
        String tradeTypeStr = resMap.get("trade_type").toString();

        Object codeUrlStr = resMap.get("code_url");
        String codeUrl = null;
        if (!ObjectUtils.isEmpty(codeUrlStr)) {
            codeUrl = codeUrlStr.toString();
        }

        Validate.isTrue(configMchId.equals(mchId),"无效的mchId");

        SortedMap<String, String> params = new TreeMap<String, String>();
        for (Object name : resMap.keySet()) {
            params.put(name.toString(), resMap.get(name).toString());
        }
        if (!validSign(params, sign,configMchSecret)) {
            log.info("签名验证失败...");
            throw new IllegalArgumentException("微信校验异常");
        }

        String timestamp = String.valueOf((int) (System.currentTimeMillis() / 1000));
        Map<String, String> temParam = new HashMap<>();
        temParam.put("appid", appId);
        //temParam.put("mchid", mchId);
        temParam.put("partnerid", mchId);
        temParam.put("noncestr", nonceStr);
        temParam.put("timestamp", timestamp);
        temParam.put("prepayid", prepayId);
        temParam.put("package", "Sign=WXPay");

        String newSign = WXPayUtil.generateSignature(temParam,
                configMchSecret, SignType.HMACSHA256);
        temParam.put("sign", newSign);

        log.info("nonceStr-{},timestamp-{},sign-{}", nonceStr, timestamp, newSign);

        resMap = temParam;

        log.info("wechat pay return last map-{}", JSON.toJSONString(resMap));

//        return new SendPrepayResult(prepayId, codeUrl, nonceStr, timestamp, null, resMap);

        return codeUrl;
    }

    private static boolean validSign(SortedMap<String, String> params, String sign,String mchSecret) throws Exception {

        String temp = WXPayUtil.generateSignature(
                params, mchSecret,
                SignType.HMACSHA256);

        if (sign == null || !sign.equals(temp)) {
            return false;
        }

        return true;
    }

}
