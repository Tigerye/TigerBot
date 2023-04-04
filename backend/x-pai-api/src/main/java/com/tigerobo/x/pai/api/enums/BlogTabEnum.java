package com.tigerobo.x.pai.api.enums;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum BlogTabEnum {
    FOLLOW,
    HOT,
    NEW,
    SITE,
    TWITTER,
    BIGSHOTS,
    ALGOLET,
    ;

    public static BlogTabEnum getByName(String tab){
        if (StringUtils.isEmpty(tab)){
            return null;
        }
        for (BlogTabEnum value : values()) {
            if (value.toString().equalsIgnoreCase(tab)){
                return value;
            }
        }
        return null;
    }
}
