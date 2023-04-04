package com.tigerobo.x.pai.api.eye.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MetricCountReq {
    @ApiModelProperty(value = "1:博客总数 2:algolet总数 3:blog源文章总数 4:bigshot总数  11:用户总数")
    List<Integer> typeList;
}
