package com.tigerobo.x.pai.api.pay.vo.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ApiBillVo {
    Integer id;

    String orderNo;
    String periodName;

    Integer billPeriod;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date payTime;
    Integer startDay;
    Integer endDay;
    BigDecimal totalFee;

    @ApiModelProperty(value = "0-不需要处理，1-待支付，2-已支付")
    Integer status;
    String statusName;
}
