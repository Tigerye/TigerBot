package com.tigerobo.pai.biz.test.service.test;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.biz.biz.process.ApiProcessor;
import com.tigerobo.x.pai.biz.serving.ApiCountService;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ApiCountServiceTest extends BaseTest {

    @Autowired
    private ApiCountService apiCountService;

    @Autowired
    private ApiProcessor apiProcessor;
    @Test
    public void countTest(){

        String id = "610762";
        int amlCount = apiCountService.getAmlCount(id);
        System.out.println(amlCount);
    }
}
