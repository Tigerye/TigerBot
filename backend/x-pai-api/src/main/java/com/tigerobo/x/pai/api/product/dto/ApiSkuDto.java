package com.tigerobo.x.pai.api.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Data
public class ApiSkuDto {
    private Integer id;
    //    Integer productId;
    String name;

    String showName;

    String desc;
    BigDecimal price;
    String unit;

    LinkedHashMap<String,Object> properties;

    Integer priceType;
}
