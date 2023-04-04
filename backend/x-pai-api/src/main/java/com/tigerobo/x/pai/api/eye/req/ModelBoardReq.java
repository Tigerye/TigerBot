package com.tigerobo.x.pai.api.eye.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ModelBoardReq {

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date endDate;
    String modelId;

    Integer userId;


    @ApiModelProperty(value = "1,-页面调用，2-api调用，3-批量预测")
    Integer callSource;

    transient Integer startDay;
    transient Integer endDay;
}
