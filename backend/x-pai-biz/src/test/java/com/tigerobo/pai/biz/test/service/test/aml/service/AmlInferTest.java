package com.tigerobo.pai.biz.test.service.test.aml.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.biz.aml.service.AmlApiServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class AmlInferTest extends BaseTest {
    @Autowired
    private AmlApiServiceImpl amlApiService;

    @Test
    public void apiExecutorTest(){
        ApiReqVo apiReqVo = new ApiReqVo();
        apiReqVo.setApiKey("81011");

        Map<String,Object> map = new HashMap<>();
        map.put("texts", Lists.newArrayList("噢，好嘞，好嘞，好可以好谢谢你啊！"));

        apiReqVo.setParams(map);

        ApiResultVo apiResultVo = amlApiService.apiExecute(apiReqVo, 3);
        System.out.println(JSON.toJSONString(apiResultVo));
    }

    @Test
    public void apiDocTest(){
        ApiReqVo apiReqVo = new ApiReqVo();
        apiReqVo.setApiKey("81011");
//        Authorization authorization = new Authorization();
//        authorization.setUid("d2c1c4f00697ac39a4d8b9a4ca189d11");
//        apiReqVo.setAuthorization(authorization);

//        Map<String,Object> map = new HashMap<>();
//        map.put("texts", Lists.newArrayList("噢，好嘞，好嘞，好可以好谢谢你啊！"));
//
//        apiReqVo.setParams(map);

        ApiResultVo apiResultVo = amlApiService.getApiDoc(apiReqVo, 3);
        System.out.println(JSON.toJSONString(apiResultVo));
    }
}
