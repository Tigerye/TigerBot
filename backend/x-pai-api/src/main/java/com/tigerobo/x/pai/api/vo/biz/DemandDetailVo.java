package com.tigerobo.x.pai.api.vo.biz;

import com.google.common.collect.Lists;
import com.tigerobo.x.pai.api.biz.entity.*;
import com.tigerobo.x.pai.api.dto.DemandDataset;
import com.tigerobo.x.pai.api.entity.IndexItem;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.PermissionTypeEnum;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-需求任务返回结果类
 * @modified By:
 * @version: $
 */
@Data
@ApiModel(value = "需求详情")
public class DemandDetailVo implements IBusinessDetailVo {
    int bizType = BusinessEnum.DEMAND.getType();
    protected Integer id;
    protected String uuid;
//    @ApiModelProperty(value = "用户页面权限角色")
//    protected Role role;
    @ApiModelProperty(value = "关联需求")
    private Demand demand;
    @ApiModelProperty(value = "标签集")
    private List<Tag> tagList;
    @ApiModelProperty(value = "数据描述集")
    private List<Dataset> fileList;

    @ApiModelProperty(value = "数据集")
    private List<DemandDataset> datasetList;

    @ApiModelProperty(value = "最佳模型")
    private Model model;
    @ApiModelProperty(value = "API接口信息")
    private API api;

    /**
     * 临时使用
     */
    Map<String,Object> task;
    @ApiModelProperty(value = "统计数据")
    private List<IndexItem> indices;
    @ApiModelProperty(value = "页面权限列表")
    private List<PermissionTypeEnum> permissionList;

    @ApiModelProperty(value = "需求阶段列表")
    List<DemandPhase> demandPhaseList;
    int callNum;


    public void addIndexItem(List<IndexItem> indexItemList) {
        if (this.indices == null)
            this.indices = Lists.newArrayList();
        this.indices.addAll(indexItemList);
    }

    public void addTag(Tag tag) {
        if (this.tagList == null)
            this.tagList = Lists.newArrayList();
        this.tagList.add(tag);
    }

//
//    @Override
//    public Integer getId() {
//        return this.task.getId();
//    }
//
//    @Override
//    public String getUuid() {
//        return this.task.getUuid();
//    }
//
//    @Override
//    public Entity.Type getType() {
//        return Entity.Type.TASK;
//    }
}
