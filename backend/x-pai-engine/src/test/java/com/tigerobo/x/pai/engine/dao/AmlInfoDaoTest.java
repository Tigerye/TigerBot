package com.tigerobo.x.pai.engine.dao;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.dal.aml.dao.AmlInfoDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlInfoDo;
import com.tigerobo.x.pai.engine.EngineBaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AmlInfoDaoTest extends EngineBaseTest {


    @Autowired
    private AmlInfoDao amlInfoDao;
    @Test
    public void loadTest(){


        AmlInfoDo infoDo = amlInfoDao.getById(20000);
        System.out.println(JSON.toJSONString(infoDo));
    }

}
