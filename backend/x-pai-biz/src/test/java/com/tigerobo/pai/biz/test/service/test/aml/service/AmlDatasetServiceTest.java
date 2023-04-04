package com.tigerobo.pai.biz.test.service.test.aml.service;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.aml.dto.AmlDatasetDto;
import com.tigerobo.x.pai.api.aml.dto.AmlStatisticDto;
import com.tigerobo.x.pai.api.aml.dto.ModelDataItem;
import com.tigerobo.x.pai.api.aml.dto.dataset.AmlDatasetItemReq;
import com.tigerobo.x.pai.biz.aml.service.AmlDatasetServiceImpl;
import com.tigerobo.x.pai.dal.aml.entity.AmlDatasetDo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AmlDatasetServiceTest extends BaseTest {

    @Autowired
    private AmlDatasetServiceImpl amlDatasetService;


    @Test
    public void getTest(){

        int id = 120873;

        final AmlDatasetDto dataset = amlDatasetService.getDataset(id);
        System.out.println(JSON.toJSONString(dataset));
    }

    @Test
    public void loadTest(){
        int id = 110;
        AmlStatisticDto amlDatasetStatistic = amlDatasetService.getAmlDatasetStatistic(id);
        System.out.println(JSON.toJSONString(amlDatasetStatistic));

        System.out.println(amlDatasetStatistic.getLabelList().get(0));
    }


    @Test
    public void loadDatasetItemPageTest(){
        int id = 4;
        AmlDatasetDo datasetDo = amlDatasetService.get(id);

        AmlDatasetItemReq req = new AmlDatasetItemReq();
        req.setPageSize(10);
        PageInfo<ModelDataItem> nasDatasetViewPage = amlDatasetService.getNasDatasetViewPage(req, datasetDo);
        List<ModelDataItem> list = nasDatasetViewPage.getList();
        System.out.println(JSON.toJSONString(list));

        System.out.println(list.get(0).getLabel());
    }
}
