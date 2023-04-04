package com.tigerobo.pai.biz.test.micro.search.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.search.api.req.property.PropertyCommitReq;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.micro.search.SearchIndexPropertyService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class IndexPropertyTest extends BaseTest {

    @Autowired
    private SearchIndexPropertyService searchIndexPropertyService;

    @Test
    public void commitPropertyTest(){

        String json = "{\"indexId\":79,\"propertyList\":[{\"propertyName\":\"id\",\"propertyType\":\"keyword\",\"format\":null,\"canSearch\":false,\"isId\":true,\"indexId\":null,\"handler\":\"\"},{\"propertyName\":\"name\",\"propertyType\":\"text\",\"format\":null,\"canSearch\":true,\"isId\":false,\"indexId\":null,\"handler\":\"\"},{\"propertyName\":\"intro\",\"propertyType\":\"text\",\"format\":null,\"canSearch\":true,\"isId\":false,\"indexId\":null,\"handler\":\"\"},{\"propertyName\":\"desc\",\"propertyType\":\"text\",\"format\":null,\"canSearch\":true,\"isId\":false,\"indexId\":null,\"handler\":\"\"},{\"propertyName\":\"image\",\"propertyType\":\"text\",\"format\":null,\"canSearch\":true,\"isId\":false,\"indexId\":null,\"handler\":\"\"},{\"propertyName\":\"style\",\"propertyType\":\"text\",\"format\":null,\"canSearch\":true,\"isId\":false,\"indexId\":null,\"handler\":\"\"}]}";
        final PropertyCommitReq propertyCommitReq = JSON.parseObject(json, PropertyCommitReq.class);

        searchIndexPropertyService.commitProperties(propertyCommitReq);
    }

    @Test
    public void getHandlerTest(){

        final List supportDataTypeList = searchIndexPropertyService.getSupportDataTypeList();
        System.out.println(JSON.toJSONString(supportDataTypeList));
    }
}
