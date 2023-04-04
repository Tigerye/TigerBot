package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum  ThumbAction {

    THUMB_UP(0),
    THUMB_DOWN(1)
    ;
    Integer type;

    ThumbAction(Integer type){
        this.type = type;
    }
}
