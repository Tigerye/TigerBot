package com.tigerobo.x.pai.api.aml.enums;

import lombok.Getter;

@Getter
public enum  DatasetMetricEnum {
    ALL_ITEM("allItem","所有标签","all.json"),
    LABELED("labeled","已加标签","labeled.json"),
    UN_LABELED("unlabeled","未加标签","unlabeled.json"),
    TRAIN("training","训练","train.json"),
    VALIDATION("validation","验证","validation.json"),
    TEST("testing","测试","testing.json"),
    ;
    private String key;
    private String name;
    private String defaultFileName;

    DatasetMetricEnum(String key,String name,String defaultFileName){
        this.key = key;
        this.name = name;
        this.defaultFileName = defaultFileName;
    }

    public static DatasetMetricEnum getByKey(String key){

        for (DatasetMetricEnum value : values()) {
            if (value.getKey().equals(key)){
                return value;
            }
        }
        return null;
    }
}
