package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum ProcessStatusEnum {
    ON_QUEUE(0,"排队中"),
    WAIT_PROCESS(1,"待处理"),
    FAIL(2,"处理失败"),
    WAIT_AUDIT(3,"待审核"),
    SUCCESS(5,"已成功"),
    ;
    Integer status;

    String text;

    ProcessStatusEnum(Integer status, String text){
        this.status = status;
        this.text = text;
    }


    public static ProcessStatusEnum getByStatus(Integer status){

        if (status == null){
            return null;
        }

        for (ProcessStatusEnum value : values()) {
            if (value.getStatus().equals(status)){
                return value;
            }
        }
        return null;


    }

    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (ProcessStatusEnum value : values()) {
            builder.append(value.getStatus()).append(":").append(value.getText()).append(";");
        }

        System.out.println(builder);
    }

}
