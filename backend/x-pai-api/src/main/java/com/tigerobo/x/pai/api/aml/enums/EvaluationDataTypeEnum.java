package com.tigerobo.x.pai.api.aml.enums;

import lombok.Getter;

@Getter
public enum  EvaluationDataTypeEnum {

    TP("TP","真正例"),
    FP("FP","假正例"),
    FN("FN","假负例"),
    ;

    String type;
    String name;
    EvaluationDataTypeEnum(String type,String name){
        this.type = type;
        this.name = name;
    }

    public static EvaluationDataTypeEnum getByType(String type){
        if (type == null){
            return null;
        }
        for (EvaluationDataTypeEnum value : values()) {
            if (value.getType().equals(type)){
                return value;
            }
        }
        return null;
    }
}
