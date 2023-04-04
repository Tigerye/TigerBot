package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum  JobStatusEnum {

    WAIT_PROCESS(0,"待处理"),
    SUCCESS(1,"已成功"),
    FAIL(2,"处理失败"),
    ;
    Integer status;

    String text;

    JobStatusEnum(Integer status,String text){
        this.status = status;
        this.text = text;
    }


    public static JobStatusEnum getByStatus(Integer status){

        if (status == null){
            return null;
        }

        for (JobStatusEnum value : values()) {
            if (value.getStatus().equals(status)){
                return value;
            }
        }
        return null;


    }

}
