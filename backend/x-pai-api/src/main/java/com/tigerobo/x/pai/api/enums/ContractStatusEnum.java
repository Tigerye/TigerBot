package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum ContractStatusEnum {
    NOT_START(0,"未开始"),
    ON_SIGN(1,"签署中"),
    SIGNED(2,"已签约"),
    CANCEL(3,"取消取消"),
    ;

    private Integer status;
    private String name;
    ContractStatusEnum(Integer status, String name){

        this.status = status;
        this.name = name;
    }


    public static ContractStatusEnum getByStatus(Integer status){

        if (status == null){
            return null;
        }
        for (ContractStatusEnum value : values()) {
            if (value.getStatus().equals(status)){
                return value;
            }
        }
        return null;

    }
}
