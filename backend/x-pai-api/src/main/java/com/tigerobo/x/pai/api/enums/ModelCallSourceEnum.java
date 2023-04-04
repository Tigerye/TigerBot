package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum ModelCallSourceEnum {
    PAGE_EXECUTE(1,"页面调用"),
    INVOKE(2,"接口调用"),
    API_BATCH_EVALUATE(4,"批量测试"),
    ;

    private Integer type;
    private String text;
    ModelCallSourceEnum(Integer type, String text){
        this.type = type;
        this.text = text;
    }

}
