package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum BlogSourceFromEnum {

    ALGOLET(0,"用户上传"),
    SITE(1,"爬虫爬取"),
    BIG_SHOT(2,"爬取twitter")

    ;

    Integer type;
    String name;
    BlogSourceFromEnum(Integer type,String name){
        this.type = type;
        this.name = name;
    }

    public static BlogSourceFromEnum getByType(Integer type){

        if (type == null){
            return null;
        }
        for (BlogSourceFromEnum value : values()) {
            if (value.getType().equals(type)){
                return value;
            }
        }
        return null;
    }

}
