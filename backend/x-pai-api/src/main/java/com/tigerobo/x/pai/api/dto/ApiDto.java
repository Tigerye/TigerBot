package com.tigerobo.x.pai.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;
@Data
public class ApiDto {
    private Integer id;
    private String uuid;

    private String name;

    private String intro;

    private String desc;

    private String image;

    private String createBy;

    transient private String uri;
    @ApiModelProperty(value = "demo")
    private Map<String, Object> demo;
    @ApiModelProperty(value = "page样列")
    private Map<String, Object> pageDemo;

    @ApiModelProperty(value = "演示样式")
    private String style;
    @ApiModelProperty(value = "状态")
    private Integer status;

    boolean supportBatch;
    @ApiModelProperty(value = "隶属模型ID")
    private Integer modelId;
    @ApiModelProperty(value = "隶属模型UUID")
    private String modelUuid;

    String baseModelUid;


    Integer originCallCount;

    Boolean showApi = true;
}