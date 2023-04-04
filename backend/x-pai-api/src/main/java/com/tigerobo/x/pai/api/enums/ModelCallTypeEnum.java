package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum ModelCallTypeEnum {
    APP(1,"应用"),
    AML(2,"自主训练"),
    ;

    private Integer type;
    private String text;
    ModelCallTypeEnum(Integer type,String text){
        this.type = type;
        this.text =text;

    }

    public static ModelCallTypeEnum getByType(Integer type){
        if (type == null){
            return null;
        }

        for (ModelCallTypeEnum value : values()) {
            if (value.getType().equals(type)){
                return value;
            }
        }
        return null;
    }
}
