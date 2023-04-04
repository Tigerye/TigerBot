package com.tigerobo.x.pai.service.controller.task;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.auth.vo.GroupVo;
import com.tigerobo.x.pai.api.biz.entity.Task;
import com.tigerobo.x.pai.api.vo.biz.ModelVo;
import com.tigerobo.x.pai.api.vo.biz.TaskVo;
import com.tigerobo.x.pai.api.vo.biz.req.TaskDeleteFileReq;
import com.tigerobo.x.pai.api.vo.biz.req.TaskUploadFileReq;
import com.tigerobo.x.pai.api.config.ConditionConfig;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.WebRepVo;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.task.AppNameReq;
import com.tigerobo.x.pai.biz.biz.service.WebDemandService;
import com.tigerobo.x.pai.biz.biz.service.WebModelService;
import com.tigerobo.x.pai.biz.biz.service.WebTaskService;
import com.tigerobo.x.pai.biz.com.aspect.ViewRecord;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-前端服务-需求/任务接口
 * @modified By:
 * @version: $
 */
@Slf4j
@RestController
@RequestMapping("/web/task/")
@Api(value = "业务模块-前端服务-需求/任务接口", position = 2900, tags = "业务模块-前端服务-需求/任务接口")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class XPaiTaskController {


    @Autowired
    private WebTaskService webTaskService;
    @Autowired
    private WebModelService webModelService;

    @ApiOperation(value = "需求-上传附件", position = 2101)
    @PostMapping(path = {"upload_dataset"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO demandUploadDataset(@Valid @RequestBody TaskUploadFileReq requestVo) {

        Integer userId = ThreadLocalHolder.getUserId();

        webTaskService.addDataset(requestVo, userId);
        return new ResultVO(null);
    }

    @ApiOperation(value = "需求-删除数据集", position = 2102)
    @PostMapping(path = {"del_dataset"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO demandUploadDataset(@Valid @RequestBody TaskDeleteFileReq requestVo) {

        webTaskService.delDataset(requestVo);
        return new ResultVO(null);
    }

    @ApiOperation(value = "任务-详情", position = 2240)
    @PostMapping(path = {"detail"}, consumes = "application/json", produces = "application/json")
    @ViewRecord
    public TaskVo taskDetail(@Valid @RequestBody WebRepVo requestVo) {
        // TODO 任务/需求 获取
        Task task = requestVo.getOrDefault("task", Task.class, null);

        // TODO 页面信息获取
//        TaskVo taskVo = this.taskService.build(task.getId());
        String uuid = task.getUuid();
        TaskVo taskVo = webTaskService.getDetail(uuid);
//        taskVo.setPermissionList(webDemandService.getTaskDemandPermissionTypeEnums(taskVo.getDemand()));
        webTaskService.addSharerLog(uuid,requestVo.getShareUserId(), BusinessEnum.APP);
        return taskVo;
    }

    @ApiOperation(value = "简称获取任务详情")
    @PostMapping(path = {"getDetailByShortName"}, consumes = "application/json", produces = "application/json")
    @ViewRecord
    public TaskVo getDetailByShortName(@Valid @RequestBody AppNameReq requestVo) {

//        TaskVo taskVo = this.taskService.build(task.getId());
        String appShortName = requestVo.getAppShortName();
        TaskVo taskVo = webTaskService.getDetailByAppName(appShortName);
//        taskVo.setPermissionList(webDemandService.getTaskDemandPermissionTypeEnums(taskVo.getDemand()));

        if (taskVo!=null&&taskVo.getUuid()!=null){
            webTaskService.addSharerLog(taskVo.getUuid(),requestVo.getShareUserId(), BusinessEnum.APP);
        }

        return taskVo;
    }

    @ApiOperation(value = "任务-参与者列表", position = 2241)
    @PostMapping(path = {"joiner-list"}, consumes = "application/json", produces = "application/json")
    public PageInfo<GroupVo> taskJoinerList(@Valid @RequestBody WebRepVo requestVo) {
        // TODO 参数校验
        Task task = requestVo.getOrDefault("task", Task.class, null);
        if (task == null) {
            throw new IllegalArgumentException("模型人物不存在");
        }

        List<GroupVo> groupVoList = webTaskService.getTaskGroups(task.getUuid());

        // TODO 构建新的分页
        PageInfo<GroupVo> pageInfo = new PageInfo<GroupVo>(groupVoList);

        return pageInfo;
    }


    @ApiOperation(value = "任务-领取", position = 2250)
    @PostMapping(path = {"join"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ModelVo taskJoin(@Valid @RequestBody WebRepVo requestVo) {

        // TODO 参数校验
        Task task = requestVo.getOrDefault("task", Task.class, null);
        String uuid = task.getUuid();
        Preconditions.checkArgument(!StringUtils.isEmpty(uuid), "参数为空");
        Integer userId = ThreadLocalHolder.getUserId();
        ModelVo modelVo = webModelService.create(uuid, userId);

        return modelVo;
    }

}