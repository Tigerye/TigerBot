package com.tigerobo.x.pai.api.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.biz.entity.Demand;
import com.tigerobo.x.pai.api.biz.entity.Model;
import com.tigerobo.x.pai.api.biz.entity.Task;
import com.tigerobo.x.pai.api.entity.IndexItem;
import com.tigerobo.x.pai.api.enums.PermissionTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserModelVo {

    String name;
    String uuid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    User tasker;
    @ApiModelProperty(value = "API接口信息")
    private API api;

    String applicationId;

//    @ApiModelProperty(value = "任务详情")
//    private Task task;
    @ApiModelProperty(value = "关联需求")
    private Demand demand;

    @ApiModelProperty(value = "统计数据")
    private List<IndexItem> indices;

//    @ApiModelProperty(value = "最佳模型")
//    private Model model;

    //todo  文件列表

//    @ApiModelProperty(value = "模型文件")
//    private List<Dataset> modelFileList;

//
//    @ApiModelProperty(value = "API接口信息")
//    private API api;
    @ApiModelProperty(value = "页面权限列表")
    private List<PermissionTypeEnum> permissionList;

    String role;
}
