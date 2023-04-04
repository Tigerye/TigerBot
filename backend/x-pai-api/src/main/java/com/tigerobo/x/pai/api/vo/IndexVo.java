package com.tigerobo.x.pai.api.vo;

import com.tigerobo.x.pai.api.entity.IndexItem;
import com.tigerobo.x.pai.api.entity.IndexType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@ApiModel(value = "统计指标类")
public class IndexVo extends ResponseVo {
    @ApiModelProperty(value = "指标标识")
    private String uid;
    @ApiModelProperty(value = "指标名称")
    private String name;
    @ApiModelProperty(value = "指标类型")
    private IndexType type;
    @ApiModelProperty(value = "统计指标项")
    private List<IndexItem> indices;

    public void addIndex(IndexItem item) {
        if (this.indices == null)
            this.indices = new ArrayList<>();
        this.indices.add(item);
    }
}
