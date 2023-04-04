package com.tigerobo.x.pai.api.aml.enums;

import lombok.Getter;

@Getter
public enum  ModelServiceStatusEnum {

    OFFLINE(0,"已下线"),
    ONLINE(1,"已上线"),
    WAIT_OFFLINE(2,"下线中"),
    WAIT_ONLINE(3,"上线中"),
    NOT_EXIST(4,"模型异常"),
    ;

    private Integer status;
    private String name;

    ModelServiceStatusEnum(Integer status,String name){
        this.status = status;
        this.name = name;
    }

    public static ModelServiceStatusEnum getByValue(String value){
        if (value == null){
            return null;
        }
        for (ModelServiceStatusEnum modelServiceStatusEnum : values()) {
            if (modelServiceStatusEnum.toString().equals(value)){
                return modelServiceStatusEnum;
            }
        }
        return null;
    }

    public static ModelServiceStatusEnum getByStatus(Integer status){
        if (status == null){
            return null;
        }
        for (ModelServiceStatusEnum value : values()) {
            if (value.getStatus().equals(status)){
                return value;
            }
        }
        return null;

    }
}
