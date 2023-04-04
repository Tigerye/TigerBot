package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum BusinessEnum {
    DEMAND(1,"需求"),
    APP(2,"应用"),
    MODEL(3,"模型"),
    BLOG(4,"BLOG",true),
    COMMENT(5,"评论",true),
    BLOG_CHAT(6,"blog上线文"),

    ART_IMAGE(31,"艺术图",true),
    PHOTO_FIX(32,"图片修复",true),

    STYLE_TRANSFER(33,"风格迁移",true),
    SPATIO_ACTION(34,"时空动作检测",true),
    MULTI_OBJECT_TRACK(35,"多目标跟踪",true),

    GITHUB_REPO(101,"GITHUB仓库"),
    GITHUB_USER(102,"GITHUB用户"),
    ;

    Integer type;
    String text;

    boolean canNotify;
    BusinessEnum(Integer type, String text){
        this.type = type;
        this.text = text;
    }

    BusinessEnum(Integer type, String text,boolean canNotify){
        this.type = type;
        this.text = text;
        this.canNotify = canNotify;
    }
    public static String getNameByType(Integer type){
        final BusinessEnum byType = getByType(type);
        if (byType==null){
            return "";
        }
        return byType.getText();

    }

    public static BusinessEnum getByType(Integer type){
        if (type == null){
            return null;
        }
        for (BusinessEnum value : values()) {
            if (value.getType().equals(type)){
                return value;
            }
        }

        return null;
    }
}
