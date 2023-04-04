package com.tigerobo.x.pai.api.vo.biz;

import com.google.common.collect.Lists;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.biz.entity.*;
import com.tigerobo.x.pai.api.dto.DemandDataset;
import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import com.tigerobo.x.pai.api.entity.Entity;
import com.tigerobo.x.pai.api.entity.IndexItem;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.PermissionTypeEnum;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-需求任务返回结果类
 * @modified By:
 * @version: $
 */
@Data
@ApiModel(value = "模型应用")
public final class TaskVo implements IBusinessDetailVo {
    int bizType = BusinessEnum.APP.getType();
    @ApiModelProperty(value = "ID")
    protected Integer id;
    @ApiModelProperty(value = "UUID")
    protected String uuid;

    @ApiModelProperty(value = "任务详情")
    private Task task;

    @ApiModelProperty(value = "标签集")
    private List<Tag> tagList;


    @ApiModelProperty(value = "基础模型类别")
    private ModelCategoryDto baseModel;


    @ApiModelProperty(value = "API接口信息")
    private API api;
    @ApiModelProperty(value = "统计数据")
    private List<IndexItem> indices;

    int callNum;

    int viewNum;

    int shareNum;

    int commentNum;
    @ApiModelProperty(value = "用户是否评论")
    boolean userHasComment;


    int thumbUpNum;
    boolean thumbUp;


    public void addIndexItem(List<IndexItem> indexItemList) {
        if (this.indices == null)
            this.indices = Lists.newArrayList();
        this.indices.addAll(indexItemList);
    }


    public void setTask(Task task){
        this.task = task;
        this.id = task.getId();
        this.uuid = task.getUuid();
    }

}
