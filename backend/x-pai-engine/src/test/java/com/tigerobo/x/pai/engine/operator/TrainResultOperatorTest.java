package com.tigerobo.x.pai.engine.operator;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.aml.engine.dto.content.OpContent;
import com.tigerobo.x.pai.api.aml.engine.dto.train.AmlTrainInputContent;
import com.tigerobo.x.pai.api.aml.engine.dto.train.TrainResultDto;
import com.tigerobo.x.pai.engine.auto.ml.operator.model.TrainResultOperator;
import org.junit.Test;

public class TrainResultOperatorTest {

    @Test
    public void parseTest()throws Exception{

        TrainResultOperator operator = new TrainResultOperator();

        AmlTrainInputContent trainInputContent = new AmlTrainInputContent();
        String outDir = "E:\\pai\\engine\\demo\\risk\\";



//        trainInputContent.setOutputDir(outDir);
        String path = "E:\\pai\\engine\\demo\\risk\\test_results_v2.json";
        trainInputContent.setResultFilePath(path);

        OpContent opContent = new OpContent();
        opContent.setOutputRoot(outDir);

        TrainResultDto run = operator.run(trainInputContent, opContent);

        System.out.println(JSON.toJSONString(run));
    }
}
