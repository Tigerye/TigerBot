package com.tigerobo.pai.biz.test.offline;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.eye.offline.DailyMetricOfflineService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class DailyMetricOfflineServiceTest extends BaseTest {

    @Autowired
    private DailyMetricOfflineService dailyMetricOfflineService;

    @Test
    public void addMetricTest(){


        Date date = new Date();
        dailyMetricOfflineService.addDayMetrics(date);
    }
}
