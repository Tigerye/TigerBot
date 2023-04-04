package com.tigerobo.pai.biz.test.api;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.serving.ApiReqDetailService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ApiReqDetailTest extends BaseTest {

    @Autowired
    private ApiReqDetailService apiReqDetailService;

    @Test
    public void test(){

        Long reqId = 1680508687130014L;
        final Object obj = apiReqDetailService.getByReqId("31c5b5e3fc9a48f198b7403656b429b4", "973f7b67e90f4169bbe91b4dad485c9f", "6dca8f4d1e0cef4cc5806a61cbf25f9f", reqId);

        System.out.println(JSON.toJSONString(obj));
    }
}
