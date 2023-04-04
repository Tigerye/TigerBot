package com.tigerobo.pai.biz.test.pay;


import com.algolet.common.bean.vo.PageVo;
import com.algolet.pay.api.vo.AlgCoinRecordReq;
import com.algolet.pay.api.vo.AlgCoinRecordVo;
import com.algolet.pay.biz.service.ActivityService;
import com.algolet.pay.biz.service.AlgCoinRecordService;
import com.algolet.pay.biz.service.AlgCoinService;
import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PayTmpServiceTest extends BaseTest {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private AlgCoinService algCoinService;
    @Autowired
    private AlgCoinRecordService algCoinRecordService;
    @Test
    public void addTest(){


        activityService.signIn(4);
    }


    @Test
    public void countTest(){

        final int userTotalCoin = algCoinService.getUserTotalCoin(3);
        System.out.println(userTotalCoin);

    }


    @Test
    public void recordTest(){

        AlgCoinRecordReq recordReq = new AlgCoinRecordReq();

        recordReq.setUserId(3);
        recordReq.setPageSize(3);
        final PageVo<AlgCoinRecordVo> userCoinRecordPage = algCoinRecordService.getUserCoinRecordPage(recordReq);
        System.out.println(JSON.toJSONString(userCoinRecordPage));

    }
}
