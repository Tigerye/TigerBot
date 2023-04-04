package com.tigerobo.x.pai.engine.service;

import com.tigerobo.x.pai.dal.aml.dao.AmlDatasetDao;
import com.tigerobo.x.pai.dal.aml.dao.AmlInfoDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlDatasetDo;
import com.tigerobo.x.pai.dal.aml.entity.AmlInfoDo;
import com.tigerobo.x.pai.engine.EngineBaseTest;
import com.tigerobo.x.pai.engine.auto.ml.service.AmlDatasetProcessService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DatasetPrepareServiceTest extends EngineBaseTest {

    @Autowired
    private AmlDatasetProcessService amlDatasetProcessService;

    @Autowired
    private AmlInfoDao amlInfoDao;

    @Autowired
    private AmlDatasetDao amlDatasetDao;

    @Test
    public void processTest(){
        AmlInfoDo infoDo = amlInfoDao.getById(430806);
        Integer currentDataId = infoDo.getCurrentDataId();
        AmlDatasetDo dataset = amlDatasetDao.getById(currentDataId);

        amlDatasetProcessService.dealWaitProcessData(infoDo,dataset);
    }
}
