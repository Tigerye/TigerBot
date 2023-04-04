package com.tigerobo.x.pai.api.enums;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum RestFrom {
    WECHAT_SHARE(1,"微信分享");

    private Integer type;
    private String text;
    RestFrom(Integer type ,String text){
        this.type = type;
        this.text = text;
    }


    public static RestFrom getByName(String text){

        if (StringUtils.isEmpty(text)){
            return null;
        }

        for (RestFrom value : values()) {
            if (value.toString().equalsIgnoreCase(text)){
                return value;
            }
        }
        return null;
    }
}
