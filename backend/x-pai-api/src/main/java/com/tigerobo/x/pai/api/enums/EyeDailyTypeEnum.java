package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum EyeDailyTypeEnum {
    BLOG_INCR(1,"每日博客增长数"),
    BLOG_ALGOLET_INCR(2,"每日algolet增长数"),
    BLOG_SITE_INCR(3,"每日blog源文章增长"),
    BLOG_BIGSHOT_INCR(4,"每日bigshot增长"),

    USER_INCR(11,"每日新增用户数"),

    USER_ACTIVE(13,"用户活跃数"),
    ;
    Integer type;
    String text;
    EyeDailyTypeEnum(Integer type,String text){
        this.type = type;
        this.text = text;
    }

    public static EyeDailyTypeEnum getByType(Integer type){
        if (type == null){
            return null;
        }
        for (EyeDailyTypeEnum value : values()) {
            if (value.getType().equals(type)){
                return value;
            }
        }
        return null;
    }

}
