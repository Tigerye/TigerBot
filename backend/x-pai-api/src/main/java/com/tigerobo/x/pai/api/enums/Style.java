package com.tigerobo.x.pai.api.enums;

import io.swagger.annotations.ApiModel;
import lombok.Getter;


/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Getter
@ApiModel(value = "业务模块-API演示样式枚举")
public enum Style {
    UNKNOWN("未知"),
    TEXT_TO_TEXT("文本2文本",true,true),
    TEXT_TO_LABEL("文本2标签",true,true),
    TEXT_TO_ENTITY("文本2实体",true,true),
    TEXT_TO_ENTITY_REVIEW("文本2实体",true,true),
    KEY_TO_WORD("新词发现"),
    TEXT_CORRECT("文本纠错"),
    TEXT_GENERATE("文本生成",true,true),
    CONTENT_UNDERSTAND("阅读理解"),
    QA_DOMAIN("开域问答"),
    ZERO_SAMPLE_LABEL("零样本分类"),
    TEXT_TO_QA("文本生成问答"),
    IMAGE_LABEL("图片分类"),
    ART_IMAGE("生成图片"),
    PHOTO_FIX("老照片修复"),
    KEY_SEARCH("关键词搜索"),
    STYLE_TRANSFER("风格迁移"),
    OCR("图像文字识别"),
    IMAGE_ENTITY_RECOGNIZE("图像目标检测"),
    IMAGE_INSTANCE_SEGMENTATION("实例分割"),
    SPATIO_ACTION("时空动作检测"),
    MULTI_OBJECT_TRACK("多目标跟踪"),
    ;
    private String name;
    private boolean supportBatch;
    private boolean useDefaultProcess = false;


    Style(String name) {
        this.name = name;
    }

    Style(String name, boolean supportBatch) {
        this.name = name;
        this.supportBatch = supportBatch;
    }
    Style(String name, boolean supportBatch,boolean useDefaultProcess) {
        this.name = name;
        this.supportBatch = supportBatch;
        this.useDefaultProcess = useDefaultProcess;
    }

    public static Style getByValue(String name) {

        for (Style value : values()) {
            if (value.toString().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
