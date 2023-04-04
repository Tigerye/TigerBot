package com.tigerobo.pai.biz.test.eye;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.enums.MetricCountEnum;
import com.tigerobo.x.pai.api.eye.dto.EyeDayMetricDto;
import com.tigerobo.x.pai.api.eye.dto.EyeMetricCountDto;
import com.tigerobo.x.pai.api.eye.req.DailyMetricReq;
import com.tigerobo.x.pai.api.eye.req.MetricCountReq;
import com.tigerobo.x.pai.biz.eye.EyeMetricService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class EyeMetricServiceTest extends BaseTest {

    @Autowired
    private EyeMetricService metricService;


    @Test
    public void countTest(){


        MetricCountReq req = new MetricCountReq();


        for (MetricCountEnum value : MetricCountEnum.values()) {

            EyeMetricCountDto metricCount = metricService.getMetricCount(value.getType());

            System.out.println(metricCount.getType()+"\t"+metricCount.getNum());
        }



    }

    @Test
    public void dailyMetricListTest(){

        DailyMetricReq req = new DailyMetricReq();

        req.setType(1);

        List<EyeDayMetricDto> dailyMetricList = metricService.getDailyMetricList(req);

        System.out.println(JSON.toJSONString(dailyMetricList));


    }
}
