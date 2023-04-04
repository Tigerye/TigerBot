package com.tigerobo.x.pai.api.ai.enums;

import lombok.Getter;

@Getter
public enum AiArtImageProcessEnum {

    PREPARE(0,"资源准备中"),
    ON_PROCESS(1,"处理中"),
    FAIL(2,"处理失败"),
    WAIT_AUDIT(3,"待审核"),
    SUCCESS(5,"已成功"),
    ;
    Integer status;
    String text;
    AiArtImageProcessEnum(Integer status, String text){
        this.status = status;
        this.text = text;
    }


    public static String getStatusName(Integer status){

        for (AiArtImageProcessEnum value : values()) {
            if (value.getStatus().equals(status)){
                return value.getText();
            }
        }

        return "";
    }


}
