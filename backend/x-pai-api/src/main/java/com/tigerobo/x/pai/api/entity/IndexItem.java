package com.tigerobo.x.pai.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.tigerobo.x.pai.api.utils.Mapable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "指标项")
public class IndexItem implements Mapable<String, Object>, Serializable {
    @ApiModelProperty(value = "标识")
    private String uid;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "指标类型")
    private IndexType type;
    @ApiModelProperty(value = "指标内容")
    private Map<String, Object> indices;
    @ApiModelProperty(value = "子指标集")
    private List<IndexItem> subIndices;

    public void addIndexItem(IndexItem item){
        if(this.subIndices == null)
            this.subIndices = Lists.newArrayList();
        this.subIndices.add(item);
    }

    @Override
    public Map<String, Object> get() {
        return this.getIndices();
    }

    @Override
    public void set(Map<String, Object> map) {
        this.setIndices(map);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o != null && getClass() == o.getClass() && Objects.equal(uid, ((IndexItem) o).uid));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid);
    }
}