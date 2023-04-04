package com.tigerobo.x.pai.api.eye.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EyeDayMetricDto {
    String date;
    Integer day;
    @ApiModelProperty(value = "1:每日博客增长数 2:每日algolet增长数 3:每日blog源文章增长 4:每日bigshot增长 " +
            "5:每日博客总数 11:每日新增用户数 12:每日-用户总数 13:用户活跃数")
    Integer type;

    String typeName;
    Integer num;
}
