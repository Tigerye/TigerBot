package com.tigerobo.pai.biz.test.service.test;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.aml.dto.evaluate.EvaSentence;
import com.tigerobo.x.pai.api.aml.dto.evaluate.EvaluationConfidenceDto;
import com.tigerobo.x.pai.api.aml.engine.dto.train.TrainResultDto;
import com.tigerobo.x.pai.api.aml.enums.EvaluationDataTypeEnum;
import com.tigerobo.x.pai.api.aml.req.AmlConfidenceEvaluationReq;
import com.tigerobo.x.pai.biz.aml.service.AmlEvaluationServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

public class EvaluationServiceTest extends BaseTest {

    @Autowired
    private AmlEvaluationServiceImpl amlEvaluationService;

    @Test
    public void testStatistic(){
        int modelId = 3;
        List<TrainResultDto.LabelItem> labelItems = amlEvaluationService.viewStatisticEvaluation(modelId);

        System.out.println(JSON.toJSONString(labelItems));
    }

    @Test
    public void confidenceTest(){
        int modelId = 3;
        String label = "0";
        EvaluationConfidenceDto confidenceDto = amlEvaluationService.viewConfidenceEvaluation(modelId, label);

        System.out.println(JSON.toJSONString(confidenceDto));
    }

    @Test
    public void labelDataTypePageTest(){
        AmlConfidenceEvaluationReq req = new AmlConfidenceEvaluationReq();
        int modelId=2;
        req.setPageNum(2);
        req.setPageSize(6);

        req.setEvaluationDataType(EvaluationDataTypeEnum.TP.getType());
        req.setThreshold(new BigDecimal(0.5));
        req.setLabelKey("0");
        req.setLabelName("业务变动");


        PageInfo<EvaSentence> pageInfo = amlEvaluationService.viewEvaluationTypePage(modelId, req);

        System.out.println(JSON.toJSONString(pageInfo));

    }
}
