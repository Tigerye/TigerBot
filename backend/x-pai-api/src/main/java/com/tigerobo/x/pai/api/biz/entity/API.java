package com.tigerobo.x.pai.api.biz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
@ApiModel(value = "api详情")
public class API {
//    @ApiModelProperty(value = "demo")
//    private Map<String, Object> demo;
    @ApiModelProperty(value = "API样列")
    private Map<String, Object> apiDemo;
    String apiKey;
    @ApiModelProperty(value = "API样式")
    @Deprecated
    private String apiStyle;
//    @ApiModelProperty(value = "演示样式")
//    private String style;
    boolean supportBatch;

    boolean showApi;
    String baseModelUid;
    @ApiModelProperty(name = "初始调用总数")
    String originCallCountValue;
}