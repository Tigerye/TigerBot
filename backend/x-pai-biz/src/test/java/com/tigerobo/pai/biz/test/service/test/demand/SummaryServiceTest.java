package com.tigerobo.pai.biz.test.service.test.demand;

import com.alibaba.fastjson.JSON;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.biz.service.SummaryService;
import com.tigerobo.x.pai.api.vo.IndexVo;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SummaryServiceTest extends BaseTest {

    @Autowired
    private SummaryService summaryService;

    @Test
    public void indicsTEST(){

        ThreadLocalHolder.setUserId(11);
        final long start = System.currentTimeMillis();
        IndexVo indexVo = summaryService.homeIndices();

        System.out.println("delta:"+(System.currentTimeMillis()-start));
        System.out.println(JSON.toJSONString(indexVo));
    }
}
