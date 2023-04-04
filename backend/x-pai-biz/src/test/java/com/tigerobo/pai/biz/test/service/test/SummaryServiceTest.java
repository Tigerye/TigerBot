package com.tigerobo.pai.biz.test.service.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.biz.service.SummaryService;
import com.tigerobo.x.pai.api.vo.biz.mine.MineCountVo;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SummaryServiceTest extends BaseTest {

    @Autowired
    private SummaryService summaryService;

    @Test
    public void mineCountTest(){

        ThreadLocalHolder.setUserId(3);
        MineCountVo mineCount = summaryService.getMineCount();

        System.out.println(JSON.toJSONString(mineCount));
    }

    @Test
    public void test(){

    }
}
