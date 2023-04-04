package com.tigerobo.x.pai.api.aml.dto;

import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyStatisticDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AmlStatisticDto {

    List<Item> metricList = new ArrayList<>();
    List<Item> labelList = new ArrayList<>();

    String statisticDesc;
    Boolean canTrain =false;
    List<SentenceLengthRegion> sentenceLengthCountList;
    List<TextClassifyStatisticDto.SentenceLengthCount> sentenceLengthRegionCountList;
    List<Integer> regionList;
    @Data
    public static class Item{
        private String key;
        private String name;
        private int count;
        private String rate;
    }

    @Data
    public class SentenceLengthRegion{
        String name;
        Integer count;
    }
}
