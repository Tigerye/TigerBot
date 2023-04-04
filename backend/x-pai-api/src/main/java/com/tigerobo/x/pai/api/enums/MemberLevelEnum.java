package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum  MemberLevelEnum {

    MONTH(1,"月度大会员"),
    YEAR(12,"年度大会员")
    ;

    Integer level;
    String name;

    MemberLevelEnum(Integer level,String name){
        this.name = name;
        this.level = level;
    }

    public static MemberLevelEnum getByLevel(Integer level){
        if (level == null){
            return null;
        }

        for (MemberLevelEnum value : values()) {
            if (value.getLevel().equals(level)){
                return value;
            }
        }
        return null;
    }
}
