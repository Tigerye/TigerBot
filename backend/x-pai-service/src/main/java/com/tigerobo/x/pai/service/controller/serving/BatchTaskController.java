package com.tigerobo.x.pai.service.controller.serving;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.model.ModelBatchQueryReq;
import com.tigerobo.x.pai.api.vo.model.ModelBatchTaskVo;
import com.tigerobo.x.pai.biz.batch.service.BatchApiService;
import com.tigerobo.x.pai.biz.batch.service.BatchProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("batchTask")
@Api(value = "批量任务")
public class BatchTaskController {

    @Autowired
    private BatchApiService batchApiService;

    @Autowired
    private BatchProcessService batchProcessService;

    @ApiOperation(value = "批量评测列表")
    @PostMapping(path = "/getUserBatchList", consumes = "application/json", produces = "application/json")
    @Authorize
    public PageVo<ModelBatchTaskVo> getUserBatchList(@Valid @RequestBody ModelBatchQueryReq apiReqVo) {
        return batchApiService.getUserPage(apiReqVo);
    }

    @ApiOperation(value = "中止")
    @PostMapping(path = "suspend", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO suspend(@Valid @RequestBody IdReqVo idReqVo) {
        batchProcessService.suspend(idReqVo);
        return ResultVO.success();
    }

    @ApiOperation(value = "继续任务")
    @PostMapping(path = "continueTask", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO continueTask(@Valid @RequestBody IdReqVo idReqVo) {
        batchProcessService.update2waitDeal(idReqVo);
        return ResultVO.success();
    }

}
