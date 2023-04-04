package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum NotifyMessageTypeEnum {
    COMMIT_FAIL(1,"订阅失败"),
    COMMIT_SUCCESS(2,"订阅成功"),
    ART_IMAGE_SUCCESS(3,"生成艺术图成功"),
    ART_IMAGE_FAIL(4,"生成艺术图失败"),

    COMMENT(10,"评论")
    ;
    Integer type;
    String text;
    NotifyTypeEnum notifyType;
    NotifyMessageTypeEnum(Integer type, String text){
        this.type = type;
        this.text = text;
    }
    NotifyMessageTypeEnum(Integer type, String text,NotifyTypeEnum notifyType){
        this.type = type;
        this.text = text;
        this.notifyType = notifyType;
    }

    public static NotifyMessageTypeEnum getByType(Integer type){
        if (type == null){
            return null;
        }
        for (NotifyMessageTypeEnum value : values()) {
            if (value.getType().equals(type)){
                return value;
            }
        }
        return null;
    }
}
