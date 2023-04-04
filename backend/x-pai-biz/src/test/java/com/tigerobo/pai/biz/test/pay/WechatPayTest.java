package com.tigerobo.pai.biz.test.pay;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.common.util.OkHttpUtil;
import com.tigerobo.x.pai.biz.pay.constant.SignType;
import com.tigerobo.x.pai.biz.pay.util.WXPayUtil;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
@Slf4j
public class WechatPayTest {


    String appId = "wx7dce890fbc536f39";
    String mchId = "1525536381";
    String appSecret = "";
    String mchSecret = "ba2f84560e8d7eb38c5b9f637044e91c";

    String cert = "676E3B4EB130461BB5C628A062D022E3880E5B65";
    @Test
    public void prePay() throws Exception {

        Map<String, String> reqData = new HashMap<>();

        reqData.put("openid", null);
        reqData.put("trade_type", "NATIVE"); // 交易类型
        BigDecimal totalFee = new BigDecimal(1);//分
        reqData.put("total_fee", String.valueOf(totalFee.longValue())); // 交易金额，单位为分

        String tradeId = "AG00002";
        String ip = "172.18.96.1";

        String notifyUrl = "https://pai-test.tigerobo.com/x-pai-biz/pay/wechat/notify";
        reqData.put("notify_url", notifyUrl); // 通知地址
        reqData.put("out_trade_no", tradeId); // 商户订单号
        reqData.put("spbill_create_ip", ip); // 订单生成的机器IP，指用户浏览器端IP
        reqData.put("body", "algolet-test月卡");

        fillRequestData(reqData);

        log.info("wechat pay request-{}", JSON.toJSONString(reqData));

        requestPost(reqData);


    }

    public void fillRequestData(Map<String, String> reqData) throws Exception {
        reqData.put("appid", appId);
        //TODO:微信app支付 appid特殊处理
        reqData.put("appid", appId);
        reqData.put("mch_id", mchId);
        reqData.put("nonce_str", WXPayUtil.generateNonceStr());

        reqData.put("sign_type", SignType.HMACSHA256.getCode());

        reqData.put("sign", WXPayUtil.generateSignature(reqData, mchSecret, SignType.HMACSHA256));

    }
    private void requestPost(Map<String, String> reqData) throws Exception {

        String reqBody = WXPayUtil.mapToXml(reqData);

        String payUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        String s = RestUtil.postStr(payUrl, reqBody,null);


        System.out.println("result:"+s);

    }
}
