package com.tigerobo.x.pai.api.dto.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ModelCategoryDto {
    @ApiModelProperty(value = "唯一标识")
    private String uid;
    @ApiModelProperty(value = "文本")
    private String text;
    @ApiModelProperty(value = "描述")
    private String desc;
    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "大类型,1-文本；2-图像")
    private Integer type;
}
