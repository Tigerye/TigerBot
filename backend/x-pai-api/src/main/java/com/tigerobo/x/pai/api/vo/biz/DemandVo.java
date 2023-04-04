package com.tigerobo.x.pai.api.vo.biz;

import com.google.common.collect.Lists;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.biz.entity.Demand;
import com.tigerobo.x.pai.api.biz.entity.Tag;
import com.tigerobo.x.pai.api.biz.entity.Task;
import com.tigerobo.x.pai.api.entity.Entity;
import com.tigerobo.x.pai.api.entity.IndexItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-业务需求返回结果类
 * @modified By:
 * @version: $
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "业务模块-业务需求返回结果类")
public class DemandVo {

    @ApiModelProperty(value = "ID")
    protected Integer id;
    @ApiModelProperty(value = "UUID")
    protected String uuid;
    @ApiModelProperty(value = "类型")
    protected Entity.Type type = Entity.Type.DEMAND;
    @ApiModelProperty(value = "用户页面权限角色")
    protected Role role;

    @ApiModelProperty(value = "需求详情")
    private Demand demand;
    @ApiModelProperty(value = "标签集")
    private List<Tag> tagList;
    @ApiModelProperty(value = "需求任务集")
    private List<Task> taskList;
    @ApiModelProperty(value = "统计数据")
    private List<IndexItem> indices;

    public void addTag(Tag tag) {
        if (this.tagList == null)
            this.tagList = Lists.newArrayList();
        this.tagList.add(tag);
    }

    public void addIndexItem(List<IndexItem> indexItemList) {
        if (this.indices == null)
            this.indices = Lists.newArrayList();
        this.indices.addAll(indexItemList);
    }
}
