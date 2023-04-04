package com.tigerobo.pai.biz.test.service.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.serving.service.ApiService;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.biz.serving.ApiKeyService;
import com.tigerobo.x.pai.biz.serving.ApiServiceImpl;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.ExecutorFactory;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiServiceTest extends BaseTest {

    @Autowired
    private ApiServiceImpl apiService;
    @Autowired
    private ApiDao apiDao;

    @Autowired
    private ExecutorFactory executorFactory;

    @Autowired
    private ApiKeyService apiKeyService;

    @Test
    public void getKeyTest(){

        final ExecutorService executorService = Executors.newFixedThreadPool(1);

        final String artImageApiKey = apiKeyService.getArtImageApiKey();
        System.out.println(artImageApiKey);
    }


    @Test
    public void demoTest(){

        ApiReqVo reqVo = new ApiReqVo();

        String url = "https://x-pai.oss-cn-shanghai.aliyuncs.com/model/multi_object_tract/sample/demo.mp4";

//        url = "";
        String demo = "{\"t\":\""+url+"\"}";
        reqVo.setParams(JSON.parseObject(demo));
        reqVo.setAppId("ba78b5ba8483a0a40ad0b480b82b2916");
        String apiKey = "8eefd7c8d8fb45af8e2b928877229ab1";
        reqVo.setApiKey(apiKey);
        reqVo.setAccessToken("e345ae8d3b7e78fcc392d80084a42a27");
        ApiResultVo execute = apiService.execute(reqVo);
        System.out.println(JSON.toJSONString(execute));
    }

    @Test
    public void factoryTest(){

//        executorFactory.doInit();
        Executable executable = executorFactory.get("2773b17d46b9451891e418bd8693ca68");

        System.out.println(executable.getApiKey());
        System.out.println(JSON.toJSONString(executable));



    }

    @Test
    public void countTest(){

        int i = apiDao.countOnlineList();
        System.out.println(i);
    }

    @Test
    public void executeTest(){

        ApiReqVo reqVo = new ApiReqVo();
        reqVo.setApiKey("42c4bfb5d63f4268808db2d77a0ed6da");
        reqVo.setAppId("d2c1c4f00697ac39a4d8b9a4ca189d11");
        String demo = "{\"url\":\"https://x-pai.oss-cn-shanghai.aliyuncs.com/model/multi_object_tract/sample/demo.mp4\"}";

        ThreadLocalHolder.setUserId(3);
        Map<String,Object> demoParam = JSON.parseObject(demo);

        reqVo.setParams(demoParam);
        ApiResultVo execute = apiService.executeInner(reqVo);

        System.out.println(JSON.toJSONString(execute));
    }

    @Test
    public void getApiDocTest()throws Throwable{

        String json = "{\"apiKey\":\"6ebd9432333741c5870a14a22d4c58c1\",\"searchIndexId\":\"\",\"authorization\":{\"token\":\"d4e33602911772b9e4f885a69614c74c\"}}";

        ThreadLocalHolder.setUserId(98);
        ApiReqVo reqVo = JSON.parseObject(json, ApiReqVo.class);
        ApiResultVo apiDoc = apiService.getApiDoc(reqVo);



        System.out.println(JSON.toJSONString(apiDoc));
    }



}
