package com.tigerobo.x.pai.dal.pay.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "payment_api_detail")
@Data
public class PaymentApiDetailPo {

    @Id
    Integer id;
    Integer userId;
    Integer agreementId;
    Integer day;
    String modelId;


    Long callNum;

    BigDecimal itemPrice;
    BigDecimal totalFee;

    Boolean isDeleted;

    Integer consumeType;


    //0-不处理，1-待处理调用数，2-已结算调用数,3处理失败
    Integer status;
}
