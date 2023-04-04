package com.tigerobo.x.pai.api.aml.dto.evaluate;

import com.tigerobo.x.pai.api.aml.engine.dto.train.TrainResultDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class EvaluationConfidenceDto {
    List<TrainResultDto.ThresholdItem> thresholdItems;
    TrainResultDto.ThresholdItem defaultItem;
    int testCount;

    BigDecimal avgPrecision;
    LinkedHashMap<String,String> labelMap;
    List<TrainResultDto.ConfuseLine> confuseMatrix;
}
