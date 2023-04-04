package com.tigerobo.pai.biz.test.service.test.aml.api;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.biz.aml.service.AmlApiServiceImpl;
import com.tigerobo.x.pai.biz.lake.LakeInferService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BatchExecutorTest extends BaseTest {

    @Autowired
    private LakeInferService lakeInferService;

    @Autowired
    private AmlApiServiceImpl amlApiService;
    @Test
    public void test()throws Exception{


        ApiReqVo apiReqVo = new ApiReqVo();
        apiReqVo.setApiKey("81073");

        Map<String,Object> map = new HashMap<>();

        String path = "https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/1-202111061611.xlsx";
        path = "https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/1-202111061611.xlsx";
//        path = "/mnt/黑产.xlsx";
        path = "/tmp/oss/is/1.csv";
        map.put("filePath", path);

        apiReqVo.setParams(map);
        ApiResultVo apiResultVo = amlApiService.batchEvaluate(apiReqVo, 3);

        System.out.println(JSON.toJSONString(apiResultVo));
    }

    @Test
    public void jsonApiTest(){
        String json = "{\"apiKey\":\"81073\",\"params\":{\"batchSize\":200,\"filePath\":\"https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/1-202111061611.xlsx\"},\"authorization\":{\"token\":\"d46f3f036cb0a722ef1c0f91f4b06fe0\",\"uid\":\"d2c1c4f00697ac39a4d8b9a4ca189d11\",\"gid\":null}}";


    }
}
