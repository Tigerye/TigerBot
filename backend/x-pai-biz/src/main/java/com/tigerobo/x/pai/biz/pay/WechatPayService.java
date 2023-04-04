package com.tigerobo.x.pai.biz.pay;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.pay.dto.OrderDto;
import com.tigerobo.x.pai.api.pay.enums.OrderStatusEnum;
import com.tigerobo.x.pai.biz.pay.constant.SignType;
import com.tigerobo.x.pai.biz.pay.util.WXCheckUtil;
import com.tigerobo.x.pai.biz.pay.util.WXPayUtil;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
@Service
public class WechatPayService {


    @Value("${pai.pay.wx.mp.appId}")
    String appId;
    @Value("${pai.pay.wx.mchId}")
    String  mchId;
    @Value("${pai.pay.wx.mchSecret}")
    String mchSecret;
    @Value("${pai.pay.wx.notifyUrl}")
    String notifyUrl;

    String payUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    BigDecimal fenMulti = new BigDecimal(100);

    String SIGN_TYPE = "HMAC-SHA256";


    @Autowired
    private OrderService orderService;




    public String preOrderPayQr(Long orderNo)throws Exception{

        OrderDto order = orderService.getOrderByOrderNo(orderNo);
        Validate.isTrue(order!=null,"订单不存在");

        Validate.isTrue(OrderStatusEnum.WAIT_PAY.getStatus().equals(order.getStatus()),"订单状态不正确");

        Integer userId = ThreadLocalHolder.getUserId();
        Validate.isTrue(order.getUserId().equals(userId),"非本人订单不能支付");
        BigDecimal totalPrice = order.getTotalPrice();
        String codeUrl = prePayQr(totalPrice, orderNo, order.getName());

        Validate.isTrue(StringUtils.isNotBlank(codeUrl),"生成微信付款码失败");
        return codeUrl;
    }

    public String prePayQr(BigDecimal fee,Long orderNo,String bodyName) throws Exception {

        Map<String, String> reqData = new HashMap<>();

        reqData.put("openid", null);
        reqData.put("trade_type", "NATIVE"); // 交易类型

        BigDecimal totalFee = fee.multiply(fenMulti).setScale(0);

        reqData.put("total_fee", String.valueOf(totalFee.longValue())); // 交易金额，单位为分

        String ip = ThreadLocalHolder.getIp();
        if (StringUtils.isBlank(ip)){
            ip = "127.0.0.1";
        }

        reqData.put("notify_url", notifyUrl); // 通知地址
        reqData.put("out_trade_no", String.valueOf(orderNo)); // 商户订单号
        reqData.put("spbill_create_ip", ip); // 订单生成的机器IP，指用户浏览器端IP
        reqData.put("body", bodyName);

        fillRequestData(reqData);

        log.info("wechat pay request-{}", JSON.toJSONString(reqData));

        String response = requestPost(reqData);

        log.info("respones:orderNo:{},resp:{}",orderNo,response);
        String nonceStr = reqData.getOrDefault("nonce_str", "");
        if (StringUtils.isNotBlank(response)){

            return WXCheckUtil.parseQrPayResp(response,nonceStr,this.mchId,this.mchSecret);
        }
        return null;
    }


    public void fillRequestData(Map<String, String> reqData) throws Exception {
        //TODO:微信app支付 appid特殊处理
        reqData.put("appid", appId);
        reqData.put("mch_id", mchId);
        reqData.put("nonce_str", WXPayUtil.generateNonceStr());

        reqData.put("sign_type", SignType.HMACSHA256.getCode());

        reqData.put("sign", WXPayUtil.generateSignature(reqData, mchSecret, SignType.HMACSHA256));
    }

    private String requestPost(Map<String, String> reqData) throws Exception {

        String reqBody = WXPayUtil.mapToXml(reqData);

        String s = RestUtil.postStr(payUrl, reqBody, MediaType.APPLICATION_XML);

        return s;

    }

    public boolean validNotifyData(SortedMap<String, String> params) {

        String appId = params.get("appid");
        String mchId = params.get("mch_id").toString();
        String sign = params.get("sign").toString();

        String tradeType = params.get("trade_type").toString();
        if (org.springframework.util.StringUtils.isEmpty(tradeType)) {
            log.error("无效的tradeType");
            return false;
        }

        if (!this.appId.equals(appId)) {
            log.error("非App支付回调,无效的appId,{}",appId);
            return false;
        }

        if (!this.mchId.equals(mchId)) {
            log.error("无效的mchId:{}",mchId);
            return false;
        }

        try {
            return validSign(params, sign);
        } catch (Exception e) {
            log.error("校验参数失败");
            return false;
        }
    }

    private boolean validSign(SortedMap<String, String> params, String sign) throws Exception {

        String temp = WXPayUtil.generateSignature(
                params, mchSecret,
                SignType.HMACSHA256);

        if (sign == null || !sign.equals(temp)) {
            return false;
        }

        return true;
    }
}
