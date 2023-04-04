package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum  ContractChannelEnum {

    QIYUESUO(1,"契约锁"),
    OFFLINE(2,"线下")
    ;

    private Integer type;
    private String text;
    ContractChannelEnum(Integer type,String text){
        this.type = type;
        this.text = text;
    }

}
