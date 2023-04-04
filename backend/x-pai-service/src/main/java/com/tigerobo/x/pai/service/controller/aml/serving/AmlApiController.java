package com.tigerobo.x.pai.service.controller.aml.serving;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.biz.aml.service.AmlApiServiceImpl;
import com.tigerobo.x.pai.biz.batch.service.BatchApiService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/aml/api/")
@Api(value = "自主训练-API请求接口", position = 5200, tags = "自主训练-API请求接口")
public class AmlApiController {

    @Autowired
    private AmlApiServiceImpl amlApiService;

    @Autowired
    private BatchApiService batchApiService;

    @ApiOperation(value = "自主训练接口演示", position = 30)
    @PostMapping(path = "/execute", consumes = "application/json", produces = "application/json")
    @Authorize
    public ApiResultVo execute(@Valid @RequestBody ApiReqVo apiReqVo) {
        Integer userId = ThreadLocalHolder.getUserId();
        return this.amlApiService.apiExecute(apiReqVo, userId);
    }


    @ApiOperation(value = "批量评测-同步")
    @PostMapping(path = "/batch-evaluate", consumes = "application/json", produces = "application/json")
    @Authorize
    public ApiResultVo batchEvaluate(@Valid @RequestBody ApiReqVo apiReqVo) throws Throwable {
        Integer userId = ThreadLocalHolder.getUserId();
        return this.amlApiService.batchEvaluate(apiReqVo, userId);
    }
}
