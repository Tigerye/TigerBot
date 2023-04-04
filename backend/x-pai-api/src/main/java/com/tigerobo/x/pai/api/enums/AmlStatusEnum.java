package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum  AmlStatusEnum {

    WAIT_UPLOAD_DATA((byte)0,"待上传数据"),
    WAIT_PROCESS_DATA((byte)1,"数据处理中"),
    PROCESS_DATA_FAIL((byte)2,"处理数据失败"),
    PROCESS_DATA_SUCCESS((byte)3,"数据处理成功"),
    WAIT_TRAIN((byte)4,"待训练","资源准备中"),
    ON_PROCESS_TRAIN((byte)5,"训练中"),
    TRAIN_FAIL((byte)6,"训练失败"),
    TRAIN_SUCCESS((byte)7,"训练成功"),
    ;

    Byte status;
    String name;
    String showName;
    AmlStatusEnum(Byte status,String name){
        this.status = status;
        this.name = name;
    }
    AmlStatusEnum(Byte status,String name,String showName){
        this.status = status;
        this.name = name;
        this.showName = showName;
    }
    public static AmlStatusEnum getByStatus(Byte status){
        if (status == null){
            return null;
        }
        for (AmlStatusEnum value : values()) {
            if (value.getStatus().equals(status.byteValue())){
                return value;
            }
        }
        return null;
    }
}
