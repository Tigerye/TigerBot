package com.tigerobo.x.pai.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum BatchTaskEnum {

    WAIT_DEAL(0,"处理中"),
    FINISH(1,"处理成功"),
    ERROR(2,"失败"),
    SUSPEND(10,"中止"),

    ;

    private Integer status;
    String text;

    public static BatchTaskEnum getByStatus(Integer status){

        if (status == null){
            return null;
        }

        for (BatchTaskEnum value : values()) {
            if (value.getStatus().equals(status)){
                return value;
            }
        }
        return null;
    }

}
