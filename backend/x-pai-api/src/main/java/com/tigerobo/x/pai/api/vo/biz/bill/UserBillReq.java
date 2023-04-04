package com.tigerobo.x.pai.api.vo.biz.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserBillReq {

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date endDate;

    Integer userId;
    @ApiModelProperty(hidden = true)
    transient int startDay;
    @ApiModelProperty(hidden = true)
    transient int endDay;
    @ApiModelProperty(value = "null,全部方式，1,页面调用;2,接口调用;4,批量预测")
    Integer callSource;
}
