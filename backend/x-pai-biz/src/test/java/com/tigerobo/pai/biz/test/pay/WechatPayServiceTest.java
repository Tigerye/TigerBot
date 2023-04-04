package com.tigerobo.pai.biz.test.pay;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.pay.req.CreateOrderReq;
import com.tigerobo.x.pai.api.pay.vo.CreateOrderVo;
import com.tigerobo.x.pai.biz.pay.OrderService;
import com.tigerobo.x.pai.biz.pay.WechatPayService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.checkerframework.checker.units.qual.C;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class WechatPayServiceTest extends BaseTest {

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private OrderService orderService;

    @Test
    public void createOrderAndPayTest()throws Exception{

        ThreadLocalHolder.setUserId(3);
        CreateOrderReq req = new CreateOrderReq();
        req.setPrice(new BigDecimal("0.01"));
        req.setSkuId(1);
        CreateOrderVo order = orderService.createOrder(req);

        Long orderNo = order.getOrderNo();


        String s = wechatPayService.preOrderPayQr(orderNo);
        System.out.println(s);

    }

}
