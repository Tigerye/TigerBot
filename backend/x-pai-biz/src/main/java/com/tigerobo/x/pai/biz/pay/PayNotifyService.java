package com.tigerobo.x.pai.biz.pay;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.pay.dto.OrderDto;
import com.tigerobo.x.pai.api.pay.enums.OrderStatusEnum;
import com.tigerobo.x.pai.api.pay.enums.PayChannelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.SortedMap;
//todo  2022/10/31 异步
@Slf4j
@Service
public class PayNotifyService {


    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private OrderService orderService;
    public void wechatPayNotify(SortedMap<String, String> params) {
        log.info("wechatPayNotify:{}", JSON.toJSONString(params));
        if (!wechatPayService.validNotifyData(params)) {
            throw new IllegalArgumentException("数据校验失败");
        }
        String resultCode = params.get("result_code");
        String openid = params.get("openid");
        String tradeType = params.get("trade_type");
        String totalFee = params.get("total_fee");
        String feeType = params.get("fee_type");
        String timeEnd = params.get("time_end");
        String outTradeNo = params.get("out_trade_no");// 商户订单号
        String transactionId = params.get("transaction_id"); // 微信支付交易号

        if (!outTradeNo.matches("\\d{8,}")){

            log.error("outTradeNo:{},err",outTradeNo);
            throw new IllegalArgumentException("订单不存在");
        }
        if (!"SUCCESS".equals(resultCode)) {
            String errMsg = params.get("err_code_des");
            log.error("outTradeNo:{}, transactionId:{}, tradeType:{}, totalFee:{}===>{}",
                    outTradeNo, transactionId, tradeType, totalFee, errMsg);

//            orderService.updateOrderPayCompleted(orderDto);
            throw new IllegalArgumentException("返回码错误");
        }

        //todo 消息队列
        orderService.paySuccess(Long.parseLong(outTradeNo),transactionId, PayChannelTypeEnum.WECHAT_SCAN);
    }


}
