package com.tigerobo.x.pai.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PriceTypeEnum {

    DEFAULT_PRICE(0, "默认价格"),
    REGION_PRICE(1, "区间价格");

    private Integer type;
    private String text;

}
