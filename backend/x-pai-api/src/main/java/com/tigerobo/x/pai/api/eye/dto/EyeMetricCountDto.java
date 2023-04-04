package com.tigerobo.x.pai.api.eye.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EyeMetricCountDto {

    @ApiModelProperty(value = "1:博客总数 2:algolet总数 3:blog源文章总数 4:bigshot总数  11:用户总数")
    Integer type;
    String typeName;
    int num;
}
