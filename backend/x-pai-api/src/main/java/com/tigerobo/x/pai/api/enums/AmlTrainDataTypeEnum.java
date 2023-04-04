package com.tigerobo.x.pai.api.enums;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum AmlTrainDataTypeEnum {
    TRAIN(1,"训练"),
//    VALIDATE(2,"验证集"),
    TEST(2,"测试"),



    ;

    private Integer type;
    private String text;
    AmlTrainDataTypeEnum(Integer type,String text){
        this.type = type;
        this.text = text;
    }

    public static AmlTrainDataTypeEnum getByName(String name){
        if (StringUtils.isEmpty(name)){
            return null;
        }
        for (AmlTrainDataTypeEnum value : values()) {
            if (value.toString().equalsIgnoreCase(name)){
                return value;
            }
        }
        return null;
    }
}
