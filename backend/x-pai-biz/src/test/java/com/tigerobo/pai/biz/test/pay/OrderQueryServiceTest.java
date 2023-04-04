package com.tigerobo.pai.biz.test.pay;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.pay.dto.OrderDto;
import com.tigerobo.x.pai.api.pay.req.UserPayOrderQueryReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.pay.OrderQueryService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class OrderQueryServiceTest extends BaseTest {

    @Autowired
    private OrderQueryService orderQueryService;

    @Test
    public void queryTest(){

        Integer userId = 18;
        UserPayOrderQueryReq req = new UserPayOrderQueryReq();



        req.setStartPayTime(DateUtils.addDays(new Date(),-1));
        req.setKeyword("月");
//        req.setEndPayTime(DateUtils.addDays(new Date(),-2));

        PageVo<OrderDto> userPayOrderList = orderQueryService.getUserPayOrderList(userId, req);

        System.out.println(JSON.toJSONString(userPayOrderList));
    }
}
