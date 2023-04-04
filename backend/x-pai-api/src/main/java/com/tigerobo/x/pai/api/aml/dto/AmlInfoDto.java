package com.tigerobo.x.pai.api.aml.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.dto.FileData;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AmlInfoDto {
    Integer id;
    Integer currentDataId;
    Integer baseModelId;
    Integer currentModelId;
    Byte status;
    String statusName;
    //    String statusShowName;
    String errMsg;
    String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    AmlBaseModelDto baseModel;

    @Deprecated
    List<FileData> datasetList;
    @Deprecated
    String fileType;

    @Deprecated
    int allItemCount;
    @Deprecated
    int labeledItemCount;
    @Deprecated
    int epoch;
    @Deprecated
    int totalEpoch;
    AmlModelDto amlModel;
    AmlDatasetDto dataset;

    Integer trainType;
    Integer parentModelId;
    AmlModelDto parentModel;

    Boolean hide;

    String msg;
}
