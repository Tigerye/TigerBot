package com.tigerobo.x.pai.service.controller.web;

import com.tigerobo.x.pai.api.config.ConditionConfig;
import com.tigerobo.x.pai.api.serving.service.ApiService;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.api.vo.FileReqVo;
import com.tigerobo.x.pai.biz.oss.OSSHome;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */

@Slf4j
@RestController
@RequestMapping("/web/")
@Api(value = "业务模块-前端服务-API演示接口", position = 2900, tags = "业务模块-前端服务-API演示接口")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class XPaiApiController extends XPaiWebController {
    @Autowired
    protected ApiService apiService;
   private final static String ROLE_SESSION_NAME = "evaluation-upload";

   @Autowired
   private OSSHome ossHome;

    @ApiOperation(value = "获取上传OSS-STS", position = 32)
    @PostMapping(path = "/api/oss-token", consumes = "application/json", produces = "application/json")
    OSSHome.OssToken ossToken(@RequestBody FileReqVo fileReqVo) throws Exception {

        OSSHome.OssToken ossToken = this.ossHome.ossToken(ROLE_SESSION_NAME);
        ossToken.setObjectDir("biz/evaluation/tmp/");
        return ossToken;
    }


//    @ApiOperation(value = "批量评测", position = 31)
//    @PostMapping(path = "/api/batch-evaluate", consumes = "application/json", produces = "application/json")
//    @Authorize
    @Deprecated
    public ApiResultVo batchEvaluate(@Valid @RequestBody ApiReqVo apiReqVo) throws Throwable {
//        apiReqVo.setSource(ModelCallSource.API_BATCH_EVALUATE.getType());
//        apiReqVo.setUserId(ThreadLocalHolder.getUserId());
//        return this.apiService.evaluate(apiReqVo);
        return null;
    }
}
