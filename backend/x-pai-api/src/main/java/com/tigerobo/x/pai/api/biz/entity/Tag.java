package com.tigerobo.x.pai.api.biz.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块表-标签信息类
 * @modified By:
 * @version: $
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "业务模块-标签信息类")
public class Tag implements Comparable<Tag> {
    @ApiModelProperty(value = "唯一标识")
    private String uid;
    @ApiModelProperty(value = "文本")
    private String text;
    @ApiModelProperty(value = "文本(英文)")
    private String textEn;
    @ApiModelProperty(value = "描述")
    private String desc;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "类型")
    @Builder.Default
    private Type type = Type.UNKNOWN;
    @ApiModelProperty(value = "图片")
    private List<String> image;

    @Getter
    @AllArgsConstructor
    public enum Type {
        UNKNOWN(0, "未知", -1.0),
        INDUSTRY(101, "行业", 0.9),
        DOMAIN(102, "领域", 0.8),

        TASKS(210, "任务", 1.0),
        LIBRARIES(220, "框架", 0.3),
        DATASETS(230, "数据集", 0.5),
        STYLE(240, "展示样式", 0.4);

        private final Integer val;
        private final String name;
        private final double priority;

        public static Type valueOf(Integer val) {
            for (Type value : values()) {
                if (value.getVal().equals(val)){
                    return value;
                }
            }
            return null;
        }
    }

    @Override
    public int compareTo(Tag o) {
//        o.getType()
        return 0;
    }
}
