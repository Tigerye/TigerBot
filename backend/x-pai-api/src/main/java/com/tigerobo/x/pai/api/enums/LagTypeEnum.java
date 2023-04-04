package com.tigerobo.x.pai.api.enums;


import lombok.Getter;

@Getter
public enum LagTypeEnum {

    UNKNOWN(0,"未知"),
    EN(1,"英文"),
    ZH(2,"中文"),
    ZH_EN(3,"中英混合")
    ;
    private Integer type;
    private String name;

    LagTypeEnum(Integer type,String name){
        this.type = type;
        this.name = name;
    }

}
