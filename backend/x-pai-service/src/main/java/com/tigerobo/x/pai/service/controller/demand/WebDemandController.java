package com.tigerobo.x.pai.service.controller.demand;


import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.biz.entity.Demand;
import com.tigerobo.x.pai.api.dto.DemandDataset;
import com.tigerobo.x.pai.api.biz.entity.Task;
import com.tigerobo.x.pai.api.vo.biz.DelDemandReq;
import com.tigerobo.x.pai.api.vo.biz.DemandDetailVo;
import com.tigerobo.x.pai.api.vo.biz.TaskVo;
import com.tigerobo.x.pai.api.vo.biz.req.DemandAuditDeclineReq;
import com.tigerobo.x.pai.api.vo.biz.req.DemandDatasetReq;
import com.tigerobo.x.pai.api.vo.biz.req.DemandEvaluationReq;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.UuidVo;
import com.tigerobo.x.pai.api.vo.WebRepVo;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.biz.biz.service.DemandDatasetService;
import com.tigerobo.x.pai.biz.biz.service.WebDemandService;
import com.tigerobo.x.pai.biz.biz.service.WebTaskService;
import com.tigerobo.x.pai.biz.com.aspect.ViewRecord;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/web/demand/")
@Api(value = "需求", position = 2900, tags = "需求")
public class WebDemandController {

    @Autowired
    private DemandDatasetService demandDatasetService;
    @Autowired
    private WebDemandService webDemandService;
    @Autowired
    private WebTaskService webTaskService;


    @ApiOperation(value = "任务-详情", position = 2240)
    @PostMapping(path = {"detail"}, consumes = "application/json", produces = "application/json")
    @ViewRecord
    public DemandDetailVo taskDetail(@Valid @RequestBody WebRepVo requestVo) {
        Task task = requestVo.getOrDefault("task", Task.class, null);

//        TaskVo taskVo = this.taskService.build(task.getId());
        String uuid = task.getUuid();
        Integer shareUserId = requestVo.getShareUserId();
        DemandDetailVo detail = webDemandService.getDetail(uuid);
        detail.setPermissionList(webDemandService.getTaskDemandPermissionTypeEnums(detail.getDemand()));
        webTaskService.addSharerLog(uuid,shareUserId, BusinessEnum.DEMAND);
        return detail;
    }


    @ApiOperation(value = "需求-评审通过", position = 2130)
    @PostMapping(path = {"auditPass"}, consumes = "application/json", produces = "application/json")
    public ResultVO auditPass(@Valid @RequestBody UuidVo requestVo) {

        Integer userId = ThreadLocalHolder.getUserId();
        webDemandService.auditPass(requestVo.getUuid(),userId);
        return ResultVO.success();
    }

    @ApiOperation(value = "需求-评审不通过", position = 2130)
    @PostMapping(path = {"auditDecline"}, consumes = "application/json", produces = "application/json")
    public ResultVO auditDecline(@Valid @RequestBody DemandAuditDeclineReq requestVo) {

        Integer userId = ThreadLocalHolder.getUserId();
        webDemandService.auditDecline(requestVo.getUuid(),requestVo.getReason(),userId);
        return ResultVO.success();
    }


    @ApiOperation(value = "需求-添加需求数据集", position = 2130)
    @PostMapping(path = {"addDataset"}, consumes = "application/json", produces = "application/json")
    public ResultVO addDemandDataset(@Valid @RequestBody DemandDatasetReq requestVo) {

        Integer userId = ThreadLocalHolder.getUserId();
        demandDatasetService.uploadDatasetList(requestVo,userId);
        return ResultVO.success();
    }

    @ApiOperation(value = "需求-获取需求数据集", position = 2130)
    @PostMapping(path = {"getDemandDatasetList"}, consumes = "application/json", produces = "application/json")
    public List<DemandDataset> getDemandDataset(@RequestBody UuidVo req){
        return demandDatasetService.getDemandDatasetList(req.getUuid());
    }

    @ApiOperation(value = "需求-测试通过", position = 2130)
    @PostMapping(path = {"testPass"}, consumes = "application/json", produces = "application/json")
    public ResultVO demandTestPass(@RequestBody UuidVo req){
        Integer userId = ThreadLocalHolder.getUserId();
        webDemandService.testPass(req.getUuid(),userId);
        return ResultVO.success();
    }



    @ApiOperation(value = "需求-完成", position = 2130)
    @PostMapping(path = {"complete"}, consumes = "application/json", produces = "application/json")
    public ResultVO demandComplete(@RequestBody UuidVo req){
        Integer userId = ThreadLocalHolder.getUserId();
        webDemandService.testPass(req.getUuid(),userId);
        return ResultVO.success();
    }


    @ApiOperation(value = "需求-添加测评", position = 2130)
    @PostMapping(path = {"addEvaluation"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO addEvaluation(@RequestBody DemandEvaluationReq req){
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        webDemandService.addDemandEvaluation(req.getUuid(),req.getEvaluation());
        return ResultVO.success();
    }



    @ApiOperation(value = "需求-删除", position = 2130)
    @PostMapping(path = {"delete"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO delete(@RequestBody DelDemandReq req){
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        webDemandService.deleteDemand(req.getUuid(),req.getReason());
        return ResultVO.success();
    }

    @ApiOperation(value = "需求-状态映射列表", position = 2130)
    @GetMapping(path = {"getPhaseList"}, produces = "application/json")
    public List<Map<String,Object>> getDemandPhaseEnumList(){

        Demand.Phase[] values = Demand.Phase.values();

        List<Map<String,Object>> list = new ArrayList<>();

        for (Demand.Phase value : values) {
            if (value == Demand.Phase.CANCEL){
                continue;
            }
            Map<String,Object> map = new HashMap<>();
            map.put("value",value.getVal());
            map.put("label",value.getName());
            list.add(map);
        }
        return list;
    }
}
