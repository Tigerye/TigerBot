package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum MetricCountEnum {
    BLOG(1,"blog总数"),
    BLOG_ALGOLET(2,"algolet总数"),
    BLOG_SITE(3,"源文章总数"),
    BLOG_BIGSHOT(4,"bigshot总数"),

    USER(11,"用户总数")
    ;
    Integer type;
    String text;
    MetricCountEnum(Integer type, String text){
        this.type = type;
        this.text = text;
    }

    public static MetricCountEnum getByType(Integer type){
        if (type == null){
            return null;
        }
        for (MetricCountEnum value : values()) {
            if (value.getType().equals(type)){
                return value;
            }
        }
        return null;
    }

}
