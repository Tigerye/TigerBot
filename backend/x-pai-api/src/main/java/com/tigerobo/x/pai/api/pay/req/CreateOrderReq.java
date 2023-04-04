package com.tigerobo.x.pai.api.pay.req;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderReq {

    private Integer skuId;

    private BigDecimal price;
}
