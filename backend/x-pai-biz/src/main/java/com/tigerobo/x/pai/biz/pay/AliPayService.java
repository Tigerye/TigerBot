package com.tigerobo.x.pai.biz.pay;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.tigerobo.x.pai.api.pay.dto.OrderDto;
import com.tigerobo.x.pai.api.pay.enums.PayChannelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service
public class AliPayService {

    @Autowired
    private OrderService orderService;

    private AlipayClient alipayClient;

    @Value("${pai.pay.ali.payUrl:}")
    String aliPayUrl;
    @Value("${pai.pay.ali.payNotifyUrl:}")
    String aliPayNotifyUrl;
    @Value("${pai.pay.ali.appId:}")
    String aliAppId ;

    @Value("${pai.pay.ali.privateKey:}")
    String aliPrivateKey;

    @Value("${pai.pay.ali.pubKey:}")
    String aliPubKey;
    String aliSignType = "RSA2";
    String charset = "UTF-8";
    public synchronized void init() {

        if (alipayClient == null) {

            alipayClient =
                    new DefaultAlipayClient(aliPayUrl
                            , aliAppId
                            , aliPrivateKey
                            , "json"
                            , charset
                            , aliPubKey
                            , aliSignType);
        }
    }

    public String qrCodePay(Long orderNo,String callBackPageUrl) throws Exception {

        OrderDto orderDto = orderService.getOrderByOrderNo(orderNo);
        Assert.notNull(orderDto, "支付订单不存在");

        Integer skuId = orderDto.getSkuId();
        String name = orderDto.getName();

        AlipayTradePagePayModel tradePayModel = new AlipayTradePagePayModel();
        tradePayModel.setBody(orderDto.getName());
        tradePayModel.setSubject(orderDto.getName());
        tradePayModel.setTotalAmount(orderDto.getTotalPrice().toString());
        tradePayModel.setOutTradeNo(String.valueOf(orderNo));
        tradePayModel.setProductCode("FAST_INSTANT_TRADE_PAY");

        AlipayTradePagePayRequest tradePayRequest = new AlipayTradePagePayRequest();
        tradePayRequest.setNotifyUrl(aliPayNotifyUrl);
        tradePayRequest.setReturnUrl(callBackPageUrl);
        tradePayRequest.setBizModel(tradePayModel);

        AlipayTradePagePayResponse tradePayResponse = getAlipayClient().sdkExecute(tradePayRequest);
        if (tradePayResponse.isSuccess()) {
            log.info("orderId {} ,paying response:{}",orderNo, JSON.toJSONString(tradePayResponse));
//            log.info("orderId {} pay success", orderNo);
//            orderService.updateOrderPaying(orderNo, payChannel);
            String body = tradePayResponse.getBody();

            String payUrl = aliPayUrl+"?"+body;
            return payUrl;
        }

        log.error("alipay pay error code-{}, msg-{}, subCode-{}, subMsg-{}"
                , tradePayResponse.getCode(), tradePayResponse.getMsg()
                , tradePayResponse.getSubCode(), tradePayResponse.getSubMsg());

        throw new IllegalArgumentException("alipay error subCode-{}" + tradePayResponse.getSubCode());
    }


    private AlipayClient getAlipayClient(){
        if (alipayClient == null){
            init();
        }
        return alipayClient;
    }


    /* 实际验证过程建议商户添加以下校验。
    1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
    2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
    3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
    4、验证app_id是否为该商户本身。
    */
    public void callBackNotify(Map<String, String[]> paramMap) throws AlipayApiException {

        Map<String, String> resultMap = this.buildParamMap(paramMap);
        Validate.isTrue(!CollectionUtils.isEmpty(resultMap),"pay-ali-notifyparamMap,为空");

        boolean signVerified = AlipaySignature.rsaCheckV1(resultMap,
                aliPubKey, charset,aliSignType);
        Validate.isTrue(signVerified,"支付宝回调校验失败");

        String orderId = resultMap.get("out_trade_no");
        Validate.isTrue(org.apache.commons.lang3.StringUtils.isNotBlank(orderId),"支付宝回调订单号为空");
//            if (StringUtils.isEmpty(orderId)) {
//                log.error("支付宝回调订单号为空");
//                return "fail";
//            }

        if (!orderId.matches("\\d{6,}")){
            throw new IllegalArgumentException("out_trade_no:"+orderId+",不正确");
        }

        OrderDto orderDto = orderService.getOrderByOrderNo(Long.parseLong(orderId));
        Validate.isTrue(orderDto!=null,"支付宝回调订单不存在 orderId-"+ orderId);


        String tradeStatus = resultMap.getOrDefault("trade_status", "");
        boolean trade_success = tradeStatus.equals("TRADE_SUCCESS");
        Validate.isTrue(trade_success,"orderNo:"+orderId+",支付宝回调状态失败,tradeStatus+"+tradeStatus);
        log.info("支付宝回调成功-orderId-{}", orderId);
        String trade_no = resultMap.getOrDefault("trade_no", "");

        orderService.paySuccess(Long.parseLong(orderId),trade_no, PayChannelTypeEnum.ALIPAY_SCAN);
    }


    private Map<String, String> buildParamMap(Map<String, String[]> paramMap) {
        //1.从支付宝回调的request域中取值
        //获取支付宝返回的参数集合
        //用以存放转化后的参数集合
        Map<String, String> conversionParams = new HashMap<>();
        for (Iterator<String> iter = paramMap.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            String[] values = paramMap.get(key);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "uft-8");
            conversionParams.put(key, valueStr);
        }
        log.info("==================aliPay返回参数集合："+conversionParams);

        return conversionParams;
    }


}
