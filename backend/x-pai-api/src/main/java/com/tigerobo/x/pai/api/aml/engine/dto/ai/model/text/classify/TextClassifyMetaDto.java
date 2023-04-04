package com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify;

import lombok.Data;

import java.util.List;

@Data
public class TextClassifyMetaDto {
    List<String> inputPathList;
    String datasetId;
    String fileType;
}
