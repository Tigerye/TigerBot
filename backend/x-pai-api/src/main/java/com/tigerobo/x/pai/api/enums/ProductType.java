package com.tigerobo.x.pai.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum ProductType {

    MEMBER(1,"会员"),
    MODEL_API(2,"模型api"),
    ALG_COIN(30,"算法币")
    ;

    private Integer type;
    private String text;


    public static ProductType getByType(Integer type){
        if (type == null){
            return null;
        }

        for (ProductType value : values()) {
            if(value.getType().equals(type)){
                return value;
            }
        }
        return null;
    }

}
