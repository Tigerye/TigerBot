package com.tigerobo.x.pai.api.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ProductSkuDto {

    private Integer id;
//    Integer productId;
    String name;
    String showName;
    String desc;
    BigDecimal price;
    BigDecimal originPrice;

    Integer days;
    Integer level;


    Map<String,Object> skuProperties;
    Integer productId;


}
