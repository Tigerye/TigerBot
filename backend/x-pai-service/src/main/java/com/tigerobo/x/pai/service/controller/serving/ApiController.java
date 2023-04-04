package com.tigerobo.x.pai.service.controller.serving;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.serving.service.ApiService;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.model.ModelBatchQueryReq;
import com.tigerobo.x.pai.api.vo.model.ModelBatchTaskVo;
import com.tigerobo.x.pai.biz.serving.ApiKeyService;
import com.tigerobo.x.pai.biz.batch.service.BatchApiService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 服务模块-API请求接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/api")
@Api(value = "API请求接口", position = 3100, tags = "API请求接口")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class ApiController {
    @Autowired
    protected ApiService apiService;
    @Autowired
    private BatchApiService batchApiService;
    @Autowired
    private ApiKeyService apiKeyService;

    @ApiOperation(value = "接口演示", position = 30)
    @PostMapping(path = "/execute", consumes = "application/json", produces = "application/json")
    public ApiResultVo execute(@Valid @RequestBody ApiReqVo apiReqVo) {

        apiReqVo.setSource(ModelCallSourceEnum.PAGE_EXECUTE.getType());
        apiReqVo.setUserId(ThreadLocalHolder.getUserId());
        return this.apiService.execute(apiReqVo);
    }

    @ApiOperation(value = "生成艺术图接口演示", position = 30)
    @PostMapping(path = "/call/artImage", consumes = "application/json", produces = "application/json")
    public ApiResultVo callArtImage(@Valid @RequestBody ApiReqVo apiReqVo) {
        final String artImageApiKey = apiKeyService.getArtImageApiKey();
        apiReqVo.setApiKey(artImageApiKey);
        apiReqVo.setSource(ModelCallSourceEnum.PAGE_EXECUTE.getType());
        apiReqVo.setUserId(ThreadLocalHolder.getUserId());
        return this.apiService.execute(apiReqVo);
    }

    @ApiOperation(value = "异步批量评测")
    @PostMapping(path = "/sync/batchEvaluate", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO syncBatchEvaluate(@Valid @RequestBody ApiReqVo apiReqVo) throws Throwable {
        Integer userId = ThreadLocalHolder.getUserId();

        apiReqVo.setUserId(userId);
        return batchApiService.addTask(apiReqVo);
    }

    @ApiOperation(value = "查询批量测试结果")
    @PostMapping(path = "/getByReqId", consumes = "application/json", produces = "application/json")
    @Authorize
    public ModelBatchTaskVo getByReqId(@Valid @RequestBody ReqId req) {

        Preconditions.checkArgument(req.getReqId()!=null,"参数为空");
        return batchApiService.getByReqId(req.getReqId());
    }

    @Deprecated
    @ApiOperation(value = "批量评测列表-已过时")
    @PostMapping(path = "/getUserBatchList", consumes = "application/json", produces = "application/json")
    @Authorize
    public PageVo<ModelBatchTaskVo> getUserBatchList(@Valid @RequestBody ModelBatchQueryReq apiReqVo) {
        return batchApiService.getUserPage(apiReqVo);
    }
    @Data
    private static class ReqId{
        Long reqId;
    }
}
