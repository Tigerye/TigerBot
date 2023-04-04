package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum  DemandLogTypeEnum {

    CREATE(1,"创建需求"),
    UPDATE(2,"更新需求"),

    UPLOAD_FILE(3,"上传附件"),
    UPLOAD_DATASET(4,"上传数据集"),

    AUDIT_PASS(5,"评审通过"),
    AUDIT_DECLINE(6,"评审不通过"),
    DELETE(7,"删除")
    ;

    private Integer type;
    private String name;
    DemandLogTypeEnum(Integer type,String name){
        this.type = type;
        this.name = name;
    }
}
