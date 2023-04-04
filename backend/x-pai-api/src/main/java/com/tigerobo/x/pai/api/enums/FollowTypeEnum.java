package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum FollowTypeEnum {
    USER(0,"关注平台用户"),

    SITE(1,"来源关注"),
    BIG_SHOT(2,"bigshot"),

    GITHUB_USER(3,"github用户")
    ;
    Integer type;
    String text;
    FollowTypeEnum(Integer type,String text){
        this.type = type;
        this.text = text;
    }

    public static FollowTypeEnum getByType(Integer type){
        if (type == null){
            return null;
        }
        for (FollowTypeEnum value : values()) {
            if (value.getType().equals(type)){
                return value;
            }
        }
        return null;
    }
}
