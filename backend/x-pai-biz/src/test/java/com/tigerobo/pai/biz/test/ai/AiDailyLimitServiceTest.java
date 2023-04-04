package com.tigerobo.pai.biz.test.ai;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.ai.AiDailyLimitService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AiDailyLimitServiceTest extends BaseTest {

    @Autowired
    private AiDailyLimitService aiDailyLimitService;


    @Test
    public void setUserCountTest(){

        aiDailyLimitService.setUserCount(47,10);
    }

}
