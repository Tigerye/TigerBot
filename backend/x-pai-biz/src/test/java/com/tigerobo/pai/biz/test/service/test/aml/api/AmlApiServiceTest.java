package com.tigerobo.pai.biz.test.service.test.aml.api;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.biz.aml.service.AmlApiServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AmlApiServiceTest extends BaseTest {


    @Autowired
    private AmlApiServiceImpl amlApiService;

    @Autowired
    private UserService userService;
    @Test
    public void invokeTest(){


        String text = "{\"text\":\"我巴不得现在就打s12\"}";
        ApiReqVo apiReqVo = new ApiReqVo();

        apiReqVo.setApiKey("610762");
        apiReqVo.setParams(JSON.parseObject(text));
        apiReqVo.setAppId("8d3b7a6c18cfd7f574cf061bda343b8f");
        apiReqVo.setAccessToken("ddd");


//        userService.authorize(apiReqVo);
//        String uid ="8d3b7a6c18cfd7f574cf061bda343b8f";
//        Integer userId = userService.getIdByUuId(uid);


        Integer userId = 11;
        ApiResultVo apiResultVo = amlApiService.apiExecute(apiReqVo, userId);
        System.out.println(JSON.toJSONString(apiResultVo));
    }

    @Test
    public void invokeApiTest(){

        String text = "{\"apiKey\":\"81012\"}";
        ApiReqVo apiReqVo = JSON.parseObject(text,ApiReqVo.class);

//        userService.authorize(apiReqVo);
        String uid = "8d3b7a6c18cfd7f574cf061bda343b8f";
        Integer userId = userService.getIdByUuId(uid);

        ApiResultVo apiResultVo = amlApiService.getApiDoc(apiReqVo, userId);
        System.out.println(JSON.toJSONString(apiResultVo));
    }
}
