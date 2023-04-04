package com.tigerobo.x.pai.api.pay.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderVo {

    private Long orderNo;
    private BigDecimal price;
    private String productName;
}
