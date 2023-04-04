package com.tigerobo.x.pai.service.controller.aml.serving;

import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.serving.service.ApiService;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.biz.aml.service.AmlApiServiceImpl;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 服务接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/aml/invoke")
@Api(value = "自主训练服务模块-服务请求接口", position = 3200, tags = "自主训练服务模块-服务请求接口")
public class AmlInvokeController {

    @Autowired
    private AmlApiServiceImpl amlApiService;

    @ApiOperation(value = "接口调用", position = 50)
    @PostMapping(path = "", consumes = "application/json", produces = "application/json")
//    @Authorize(isAccessToken = true)
    public ApiResultVo invoke(HttpServletRequest request, @Valid @RequestBody JSONObject params) throws Throwable {
        String apiKey = request.getParameter("apiKey");
        String appId = request.getParameter("appId");
        String accessToken = request.getParameter("accessToken");
        ApiReqVo apiReqVo = ApiReqVo.builder().appId(appId).apiKey(apiKey).accessToken(accessToken).params(params).build();

        return this.amlApiService.serverExecute(apiReqVo);
    }

    @ApiOperation(value = "接口信息", position = 60)
    @PostMapping(path = "/api", consumes = "application/json", produces = "application/json")
    @Authorize
    public ApiResultVo api(@Valid @RequestBody ApiReqVo apiReqVo) {
        Integer userId = ThreadLocalHolder.getUserId();
        return amlApiService.getApiDoc(apiReqVo,userId);
    }


}
