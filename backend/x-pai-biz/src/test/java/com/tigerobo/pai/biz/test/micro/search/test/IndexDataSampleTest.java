package com.tigerobo.pai.biz.test.micro.search.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.search.api.dto.sample.DataSampleDto;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.micro.search.IndexDataSampleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class IndexDataSampleTest extends BaseTest {

    @Autowired
    private IndexDataSampleService indexDataSampleService;

    @Test
    public void getListTest(){

        final List<DataSampleDto> dataSample = indexDataSampleService.getDataSample();

        System.out.println(JSON.toJSONString(dataSample));
    }
}
