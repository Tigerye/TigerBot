package com.tigerobo.x.pai.engine.service;

import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.engine.EngineBaseTest;
import com.tigerobo.x.pai.engine.auto.ml.service.AmlStartTrainService;

import com.tigerobo.x.pai.engine.auto.ml.service.AmlTrainOnProcessService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TrainServiceTest extends EngineBaseTest {

    @Autowired
    private AmlStartTrainService amlStartTrainService;

    @Autowired
    private AmlModelDao amlModelDao;

    @Autowired
    private AmlTrainOnProcessService amlTrainOnProcessService;

    @Test
    public void startTrainTest()throws Exception{
        AmlModelDo modelDo = amlModelDao.getById(81001);

        amlStartTrainService.process(modelDo);
    }

    @Test
    public void onProcessTest()throws Exception{
        AmlModelDo modelDo = amlModelDao.getById(610765);
        amlTrainOnProcessService.deal(modelDo);
    }
}
