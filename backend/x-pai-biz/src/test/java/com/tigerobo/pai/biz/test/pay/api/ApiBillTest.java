package com.tigerobo.pai.biz.test.pay.api;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.pay.vo.api.ApiBillDetailStatisticVo;
import com.tigerobo.x.pai.biz.pay.bill.ApiBillService;
import com.tigerobo.x.pai.biz.pay.payment.PaymentBillService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ApiBillTest extends BaseTest {
    @Autowired
    private ApiBillService apiBillService;

    @Autowired
    private PaymentBillService paymentBillService;
    @Test
    public void addBillTest(){

        for (int day = 20220312; day <=20220314; day++) {

            int userId = 18;
            apiBillService.initUserApiDetailBill(userId,day);
        }

    }
    @Test
    public void initBillMonthTest(){

        int userId = 18;
        int month = 202203;
        apiBillService.initMonthTotal(userId,month);
    }


    @Test
    public void bill2orderTest(){
        int id = 3;
        paymentBillService.bill2order(id);

    }

    @Test
    public void getBillTest()throws Exception{

        int userId = 98;
        int month = 202203;
        final ApiBillDetailStatisticVo billStatistic = paymentBillService.getBillStatistic(userId, month);

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(billStatistic));

    }
    @Test
    public void createOrderTest(){

        paymentBillService.bill2order(3);
    }
}
