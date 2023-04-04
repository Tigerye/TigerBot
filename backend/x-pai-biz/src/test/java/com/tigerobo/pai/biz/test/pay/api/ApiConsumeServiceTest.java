package com.tigerobo.pai.biz.test.pay.api;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.pay.api.ApiBalanceCardService;
import com.tigerobo.x.pai.biz.pay.api.ApiConsumeService;
import com.tigerobo.x.pai.biz.pay.api.manage.ApiBillManage;
import com.tigerobo.x.pai.biz.pay.bill.ApiBillService;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.function.Consumer;

public class ApiConsumeServiceTest extends BaseTest {

    @Autowired
    private ApiConsumeService apiConsumeService;
    @Autowired
    private ApiBillService apiBillService;

    @Autowired

    private ApiBalanceCardService apiBalanceCardService;
    @Autowired
    private ApiBillManage apiBillManage;
    @Test
    public void consumeTest()throws Exception{

        int day = 20220316;
        int userId = 98;
        int nextMonthDay = day+5;
        Date date = DateUtils.parseDate(String.valueOf(day), "yyyyMMdd");

        final Date next = DateUtils.parseDate(String.valueOf(nextMonthDay), "yyyyMMdd");

        while (date.before(next)){
            final int dayValue = TimeUtil.getDayValue(date);

            System.out.println("day:"+dayValue);
            apiBillManage.billDetailTask(dayValue);
//            apiConsumeService.calUserModelCall(dayValue,userId);
//            apiBillService.initMonthTotal(userId, TimeUtil.getMonthValue(day));
            apiBillManage.paymentDetailBillSettlement(dayValue);
            date = DateUtils.addDays(date,1);
        }
    }


    private void deal(Consumer<Integer> consumer)throws Exception{

        int day = 20220201;
        int userId = 98;
        int nextMonthDay = 20220301;
        Date date = DateUtils.parseDate(String.valueOf(day), "yyyyMMdd");

        final Date next = DateUtils.parseDate(String.valueOf(nextMonthDay), "yyyyMMdd");

        while (date.before(next)){
            final int dayValue = TimeUtil.getDayValue(date);

            System.out.println("day:"+dayValue);

            consumer.accept(dayValue);

            date = DateUtils.addDays(date,1);
        }
    }

    @Test
    public void test(){
        int day = 20220228;
        int userId = 98;
        apiBillService.initMonthTotal(userId, TimeUtil.getMonthValue(day));
    }
}
