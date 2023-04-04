package com.tigerobo.x.pai.dal.pay.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "payment_api_bill")
public class PaymentApiBillPo {

    @Id
    Integer id;
    Integer userId;
    Integer billPeriod;
    Integer startDay;

    Integer endDay;

    BigDecimal totalFee;

    Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date payTime;

    String orderNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

    Boolean isDeleted;

}
