package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum  OrgVerifyStatusEnum {
    NOT_START(0,"未开始"),
    ON_VERIFY(1,"认证中"),
    VERIFIED(2,"已认证"),
    FAIL(3,"认证失败"),
    ;
    private Integer status;
    private String name;
    OrgVerifyStatusEnum(Integer status,String name){
        this.status = status;
        this.name = name;
    }


    public static OrgVerifyStatusEnum getByStatus(Integer status){
        if (status == null){
            return null;
        }
        for (OrgVerifyStatusEnum value : values()) {
            if (value.getStatus().equals(status)){
                return value;
            }
        }
        return null;
    }

    public static String getStatusName(Integer status){
        OrgVerifyStatusEnum statusEnum = getByStatus(status);
        if (statusEnum == null){
            return "";
        }
        return statusEnum.getName();
    }

}
