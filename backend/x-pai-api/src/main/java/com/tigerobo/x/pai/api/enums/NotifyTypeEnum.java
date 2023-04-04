package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum NotifyTypeEnum {
    SYSTEM(2,"系统消息"),
    COMMIT_MEDIA(1,"订阅通知"),
    COMMENT(3,"评论"),
    THUMB(4,"赞"),
//    FOLLOW(5,"关注"),
    ;
    Integer type;
    String text;
    NotifyTypeEnum(Integer type, String text){
        this.type = type;
        this.text = text;
    }

    public static NotifyTypeEnum getByType(Integer type){
        if (type == null){
            return null;
        }
        for (NotifyTypeEnum value : values()) {
            if (value.getType().equals(type)){
                return value;
            }
        }
        return null;
    }
}
