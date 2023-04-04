package com.tigerobo.x.pai.api.eye.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DailyMetricReq {

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date endDate;
    String modelId;

    Integer userId;
    @ApiModelProperty(value = "1:每日博客增长数 2:每日algolet增长数 3:每日blog源文章增长 4:每日bigshot增长 " +
            "11:每日新增用户数 13:用户活跃数")
    Integer type;
    transient Integer startDay;
    transient Integer endDay;
}
