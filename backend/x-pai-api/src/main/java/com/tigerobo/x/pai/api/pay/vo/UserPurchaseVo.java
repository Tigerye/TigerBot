package com.tigerobo.x.pai.api.pay.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserPurchaseVo {


    Integer userId;

    Integer productId;

    Integer skuId;
    String skuName;

    Integer amount;
    BigDecimal totalPrice;
    Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date payTime;
    Integer payChannel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;


    Boolean isDeleted;

    String paymentNo;
}
