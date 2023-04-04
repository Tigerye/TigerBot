package com.tigerobo.pai.biz.test.api;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.serving.ApiSumService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ApiSumServiceTest extends BaseTest {

    @Autowired
    private ApiSumService apiSumService;

    @Test
    public void test()throws Exception{


        for (int i = 0; i < 10; i++) {

            Integer apiTotal = apiSumService.getApiTotal();
            System.out.println(apiTotal);

            if (i<9){
                Thread.sleep(1000L);
            }
        }
    }
}
