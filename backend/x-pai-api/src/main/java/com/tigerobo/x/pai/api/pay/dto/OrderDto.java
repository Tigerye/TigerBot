package com.tigerobo.x.pai.api.pay.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderDto {
    private Long orderNo;
    private Integer userId;
    private Integer payChannel;
    private String payChannelName;

    private String name;

    private BigDecimal totalPrice;

    private Integer skuId;

    private Integer amount;

    private Integer status;

    private String statusName;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date payTime;

    String paymentNo;


}
