package com.tigerobo.pai.biz.test.dal.test;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.dal.aml.dao.AmlBaseModelDao;
import com.tigerobo.x.pai.dal.aml.dao.AmlInfoDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlBaseModelDo;
import com.tigerobo.x.pai.dal.aml.entity.AmlInfoDo;
import com.tigerobo.x.pai.dal.aml.mapper.AmlInfoMapper;

import com.tigerobo.x.pai.dal.biz.entity.TaskDatasetDo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public class AmlInfoMapperTest extends BaseTest {
    @Autowired
    private AmlInfoMapper amlInfoMapper;

    @Autowired
    private AmlInfoDao amlInfoDao;
    @Autowired
    private AmlBaseModelDao amlBaseModelDao;

    @Test
    public void baseModelTest(){

        List<AmlBaseModelDo> all = amlBaseModelDao.getAll();
        System.out.println(JSON.toJSONString(all));
    }
    @Test
    public void amlTest()throws Exception{
        Example example = new Example(AmlInfoDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",1);

        List<AmlInfoDo> amlInfoDos = amlInfoMapper.selectByExample(example);

        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println(objectMapper.writeValueAsString(amlInfoDos));

    }

    @Test
    public void insertTest(){

        AmlInfoDo infoDo = new AmlInfoDo();

        Integer insert = amlInfoDao.insert(infoDo);
        System.out.println(insert);
        System.out.println(JSON.toJSONString(infoDo));
    }
}
