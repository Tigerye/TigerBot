package com.tigerobo.pai.biz.test.service.test.aml.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.aml.dto.AmlModelDto;
import com.tigerobo.x.pai.api.aml.vo.MyAmlQueryVo;
import com.tigerobo.x.pai.biz.aml.service.AmlModelServiceImpl;
import com.tigerobo.x.pai.dal.aml.dao.AmlBaseModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlBaseModelDo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AmlModelServiceTest extends BaseTest {

    @Autowired
    private AmlModelServiceImpl amlModelService;

    @Autowired
    private AmlBaseModelDao amlBaseModelDao;


    @Test
    public void offlineTest(){


    }
    @Test
    public void getBaseModelTest(){

        Integer id = 1;
        AmlBaseModelDo fromCache = amlBaseModelDao.getFromCache(id);
        System.out.println(JSON.toJSONString(fromCache));

    }
    @Test
    public void myModelListTest(){
        MyAmlQueryVo myAmlQueryVo = new MyAmlQueryVo();

        PageInfo<AmlModelDto> myModelList = amlModelService.getMyModelList(myAmlQueryVo, "18");
        System.out.println(JSON.toJSONString(myModelList.getList()));
        System.out.println(myModelList.getList().size());
        System.out.println(myModelList.getTotal());
    }
}
