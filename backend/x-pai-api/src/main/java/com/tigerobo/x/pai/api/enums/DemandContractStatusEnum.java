package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum DemandContractStatusEnum {
    NOT_START(0,"未开始"),
    ON_SIGN(1,"签署中"),
    SIGNED(2,"已签约"),
    CANCEL(3,"取消取消"),
    ;

    private Integer status;
    private String name;
    DemandContractStatusEnum(Integer status,String name){

        this.status = status;
        this.name = name;
    }


    public static DemandContractStatusEnum getByStatus(Integer status){

        if (status == null){
            return null;
        }
        for (DemandContractStatusEnum value : values()) {
            if (value.getStatus().equals(status)){
                return value;
            }
        }
        return null;

    }
}
