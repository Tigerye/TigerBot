package com.tigerobo.x.pai.engine.aml;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyMetaDto;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyOpContentDto;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyStatisticDto;
import com.tigerobo.x.pai.engine.auto.ml.pipeline.text.classify.TextClassifyPreparePipeline;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TCDatasetTest {


    @Test
    public void textClassifyDatasetTest() {
        TextClassifyPreparePipeline pipeline = new TextClassifyPreparePipeline();
        TextClassifyMetaDto metaDto = new TextClassifyMetaDto();
        String datasetId = "d1111";
        metaDto.setDatasetId(datasetId);

        String base = "/mnt/xpai/";

//        String first = base +"hongguan-1630660676006.csv";
//        String second = base +"industry-1630660669463.csv";
//        String first = base +"risk_big.csv";
        String first = base + "hongguan2.csv";
        List<String> pathList = Arrays.asList(first);
        metaDto.setInputPathList(pathList);
        metaDto.setFileType("csv");

        TextClassifyOpContentDto contentDto = new TextClassifyOpContentDto();
        contentDto.setOutputRoot("/mnt/xpai/11/" + datasetId + "/");

        TextClassifyStatisticDto run = pipeline.run(metaDto, contentDto);
        System.out.println(JSON.toJSONString(run));
    }

}
