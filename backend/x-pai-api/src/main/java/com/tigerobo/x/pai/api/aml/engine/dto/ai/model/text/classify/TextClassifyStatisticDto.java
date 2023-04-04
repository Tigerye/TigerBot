package com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify;

import lombok.Data;

import java.util.List;

//数据集统计
@Data
public class TextClassifyStatisticDto {

    /**
     * 指标统计，如全部数量，打标数量,
     * @see com.tigerobo.x.pai.api.aml.enums.DatasetMetricEnum
     */
    List<Item> metricList;
    /**
     * 标签统计
     */
    List<Item> labelList;
    String statisticPath;

    /**
     * 标签名称列表
     */
    List<String> labelNameList;

    /**
     * 区间数量统计
     */
    List<SentenceLengthCount> sentenceLengthCountList;
    @Data
    public static class Item{
        String key;
        String path;
        Integer count;
    }


    /**
     * (0,20]
     */
    @Data
    public static class SentenceLengthCount {
        int start;
        int count;
    }
}
