package com.tigerobo.pai.biz.test.service.test.aml.service;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.aml.req.AmlConfidenceEvaluationReq;
import com.tigerobo.x.pai.biz.aml.service.AmlEvaluationServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class AmlEvaluationServiceTest extends BaseTest {
    @Autowired
    private AmlEvaluationServiceImpl amlEvaluationService;

    @Test
    public void downloadTest(){
        Integer modelId = 81075;
        AmlConfidenceEvaluationReq req = new AmlConfidenceEvaluationReq();
        req.setLabelName("偷税漏税");
        req.setId(20106);
        req.setLabelKey("10");
        req.setEvaluationDataType("TP");
        req.setThreshold(new BigDecimal("0.5"));
        amlEvaluationService.innerDownloadEvaluationType(modelId,req);
    }
}
