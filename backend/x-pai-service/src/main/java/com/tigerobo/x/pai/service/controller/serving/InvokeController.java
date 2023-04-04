package com.tigerobo.x.pai.service.controller.serving;

import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.serving.service.ApiService;
import com.tigerobo.x.pai.api.serving.vo.ApiDetailVo;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.biz.serving.ApiKeyService;
import com.tigerobo.x.pai.biz.serving.ApiReqDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 服务接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/invoke")
@Api(value = "服务模块-服务请求接口", position = 3200, tags = "服务模块-服务请求接口")
public class InvokeController {
    @Autowired
    private ApiService apiService;
    @Autowired
    private ApiKeyService apiKeyService;
    @Autowired
    private ApiReqDetailService apiReqDetailService;


    @ApiOperation(value = "接口调用", position = 50)
    @PostMapping(path = "", consumes = "application/json", produces = "application/json")
//    @Authorize(isAccessToken = true)
    public ApiResultVo invoke(HttpServletRequest request, @Valid @RequestBody JSONObject params)  {
        String apiKey = request.getParameter("apiKey");
        String appId = request.getParameter("appId");
        String accessToken = request.getParameter("accessToken");

        //todo 校验token

        ApiReqVo apiReqVo = ApiReqVo.builder()
                .apiKey(apiKey)
                .appId(appId)
                .accessToken(accessToken)
                .params(params)
                .source(ModelCallSourceEnum.INVOKE.getType())
//                .userId(userDo.getId())
                .build();
        return this.apiService.executeServing(apiReqVo);
    }


    @ApiOperation(value = "reqId获取详情", position = 50)
    @PostMapping(path = "/getReqDetail", consumes = "application/json", produces = "application/json")
//    @Authorize(isAccessToken = true)
    public ApiDetailVo getReqDetail(HttpServletRequest request, @Valid @RequestBody JSONObject params)  {
        String apiKey = request.getParameter("apiKey");
        String appId = request.getParameter("appId");
        String accessToken = request.getParameter("accessToken");

        final Long reqId = params.getLong("reqId");

        return apiReqDetailService.getByReqId(appId, apiKey, accessToken, reqId);
    }

    @ApiOperation(value = "接口信息-文档", position = 60)
    @PostMapping(path = "/api", consumes = "application/json", produces = "application/json")
    @Authorize
    public ApiResultVo api(@Valid @RequestBody ApiReqVo apiReqVo) {

        return apiService.getApiDoc(apiReqVo);
    }



}
