package com.tigerobo.pai.biz.test.service.test;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.QueryVo;
import org.junit.Test;

public class ModelServiceTest extends BaseTest {




    @Test
    public void detailTest()throws Exception{

        String json = "{\"params\":{\"model\":{\"uuid\":\"machine-translation\"}},\"authorization\":{\"token\":\"c4d67d898c9cc8ce77ee610854aafd92-75ddb5778e9fe46fd17360dc73fd2afd-1000\",\"uid\":\"d2c1c4f00697ac39a4d8b9a4ca189d11\",\"gid\":\"94b351225d793977cc93897771e35209\"}}";
        QueryVo queryVo = JSON.parseObject(json, QueryVo.class);
//        ModelVo detail = modelService.detail(queryVo);

        ObjectMapper objectMapper = new ObjectMapper();
//        System.out.println(objectMapper.writeValueAsString(detail));

    }
}
