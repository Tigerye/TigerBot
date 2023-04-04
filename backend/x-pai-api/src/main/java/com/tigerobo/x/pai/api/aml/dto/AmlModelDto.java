package com.tigerobo.x.pai.api.aml.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.biz.entity.API;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AmlModelDto {

    API api;
    private Integer modelId;
    private Integer amlId;
    Integer dataId;
    Integer baseModelId;
    Byte status;
    String name;

    Integer labeledCount;
    Integer trainCount;

    AmlInfoDto amlInfo;

    AmlDatasetDto dataset;

    BigDecimal precision;
    BigDecimal recall;
    BigDecimal avgPrecision;
    private String style;
    @ApiModelProperty(value = "模型服务状态0-未开始，1-可调用")
    Integer serviceStatus;

    String serviceStatusName;

    Integer epoch;
    Integer totalEpoch;
    @ApiModelProperty(value = "模型训练完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date modelFinishTime;

//
    @ApiModelProperty(value = "模型创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    @ApiModelProperty(value = "模型更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

    AmlBaseModelDto baseModel;

    Integer parentModelId;
    AmlModelDto parentModel;
    String errMsg;
}
