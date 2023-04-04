package com.tigerobo.x.pai.api.aml.engine.dto.train;

import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class TrainResultDto {

    /**
     * 置信度映射
     */
    LinkedHashMap<String,List<ThresholdItem>> labelConfidenceThresholdMap;
//    Map<String, ThresholdItem> labelDefaultThresholdMap;

//    Map<String,BigDecimal> labelAvgPrecisionMap;

    /**
     * id-label映射
     */
    LinkedHashMap<String,String> labelMap;

    List<LabelItem> avgPrecisionList;

    List<ConfuseLine> confuseMatrix;
    List<LabelItem> confuseMatrixLabelList;

    String evaluationPath;
    String resultPath;
    String predictPath;

    @Data
    public static class LabelItem{
        String key;
        String name;
        BigDecimal avgPrecision;
    }

    @Data
    public static class ThresholdItem{
        BigDecimal threshold;
        BigDecimal recall;
        BigDecimal precision;
    }

    /**
     * 混淆矩阵每一行
     */
    @Data
    public static class ConfuseLine{
        String key;
        String name;
        int lineCount;

        List<ConfuseItem> children;
    }

    @Data
    public static class ConfuseItem{
        String targetKey;
        String targetLabel;
        int count;
        int rate;
        BigDecimal accurateRate;
    }

}
