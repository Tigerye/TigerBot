package com.tigerobo.pai.biz.test.service.test;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.auth.entity.Authorization;
import com.tigerobo.x.pai.api.vo.WebRepVo;
import org.junit.Test;

import java.util.Map;

public class ProcessorTest extends BaseTest {



    String authorJson = "{\"token\":\"c4d67d898c9cc8ce77ee610854aafd92-75ddb5778e9fe46fd17360dc73fd2afd-1000\",\"uid\":\"d2c1c4f00697ac39a4d8b9a4ca189d11\",\"gid\":\"94b351225d793977cc93897771e35209\"}";

    Authorization authorization = JSON.parseObject(authorJson, Authorization.class);
    String path = "https://x-pai.oss-cn-shanghai-internal.aliyuncs.com/biz/dataset/tmp/57cb3189d52b4df9f1b1c691808ed3e4?Expires=4780381192&OSSAccessKeyId=LTAI5t8HoYusAPr5MffHTauz&Signature=krf6D7ZIgF%2Fuq91AIt1LRx8eErY%3D";





    @Test
    public void createDemandTest() throws Exception {
        String json = "{\"params\":{\"demand\":{\"scope\":10,\"name\":\"whytest3\",\"desc\":\"\",\"intro\":\"whytest\",\"budget\":\"20万以内\",\"deliveryDate\":\"2021-08-30T16:00:00.000Z\"},\"datasetList\":[],\"tagList\":[{\"uid\":\"regulator-trader\",\"text\":\"监管机构/券商\",\"textEn\":\"Regulator & Trader\",\"type\":\"DOMAIN\"},{\"uid\":\"internet\",\"text\":\"互联网\",\"textEn\":\"Internet\",\"icon\":\"https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/tag/icon/industry-internet.png\",\"type\":\"INDUSTRY\"}]},\"authorization\":{\"token\":\"c4d67d898c9cc8ce77ee610854aafd92-75ddb5778e9fe46fd17360dc73fd2afd-1000\",\"uid\":\"d2c1c4f00697ac39a4d8b9a4ca189d11\",\"gid\":\"94b351225d793977cc93897771e35209\"}}";

        ObjectMapper objectMapper = new ObjectMapper();
        WebRepVo webRepVo = objectMapper.readValue(json, WebRepVo.class);

        Map<String, Object> params = webRepVo.getParams();



    }


}
