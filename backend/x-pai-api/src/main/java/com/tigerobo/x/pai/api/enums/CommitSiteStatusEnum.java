package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum  CommitSiteStatusEnum {
    WAIT_PROCESS(0,"待处理"),
    ON_PROCESS(1,"处理中"),
    FAIL(2,"处理失败"),
    CANCEL(3,"已取消"),
    SUCCESS(5,"处理成功")
    ;
    Integer status;
    String name;
    CommitSiteStatusEnum(Integer status,String name){
        this.status = status;
        this.name = name;
    }

    public static CommitSiteStatusEnum getByStatus(Integer status){

        if (status == null){
            return null;
        }
        for (CommitSiteStatusEnum value : values()) {
            if (value.getStatus().equals(status)){
                return value;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        for (CommitSiteStatusEnum value : values()) {
            System.out.print(value.getStatus()+":"+value.getName()+";");
        }

    }

}
