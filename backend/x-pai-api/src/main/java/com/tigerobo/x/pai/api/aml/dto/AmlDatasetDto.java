package com.tigerobo.x.pai.api.aml.dto;

import com.tigerobo.x.pai.api.dto.FileData;
import lombok.Data;

import java.util.List;

@Data
public class AmlDatasetDto {
    private Integer datasetId;

    Integer baseModelId;

    List<FileData> datasetFileList;

    String path;
    String fileType;
    Byte status;

    Integer allItemCount;

    Integer unlabeledCount;
    Integer labeledCount;

    Integer trainCount;
    Integer validationCount;
    Integer testCount;

}
