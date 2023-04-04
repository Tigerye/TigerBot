package com.tigerobo.pai.biz.test.service.test.aml.service;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlStartTrainDto;
import com.tigerobo.x.pai.api.aml.service.AmlInfoService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AmlInfoServiceTest extends BaseTest {

    @Autowired
    private AmlInfoService amlInfoService;

    @Test
    public void startTrainTest(){


        AmlStartTrainDto startTrainDto = new AmlStartTrainDto();
        startTrainDto.setId(20025);


        amlInfoService.startTrain(startTrainDto,3);
    }
}
