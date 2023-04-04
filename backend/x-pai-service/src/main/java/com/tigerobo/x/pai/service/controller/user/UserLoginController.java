package com.tigerobo.x.pai.service.controller.user;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.auth.entity.Authorization;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.uc.dto.ChangeMobileDto;
import com.tigerobo.x.pai.api.uc.dto.UserMobileRegisterDto;
import com.tigerobo.x.pai.api.uc.dto.WeChatReq;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.user.UcUserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web/uc/")
@Api(value = "用户登录模块", position = 2900, tags = "用户登录模块")
public class UserLoginController {

    @Autowired
    private UcUserServiceImpl ucUserService;

    @ApiOperation(value = "用户-手机号登录")
    @PostMapping(path = {"login_by_mobile"}, consumes = "application/json", produces = "application/json")
    public Authorization userLoginByMobile(@RequestBody UserMobileRegisterDto req) {
        return ucUserService.mobileCodeLogin(req);
    }

    @ApiOperation(value = "用户-手机号登录")
    @PostMapping(path = {"login_by_password"}, consumes = "application/json", produces = "application/json")
    public Authorization userLoginByPassword(@RequestBody UserMobileRegisterDto req) {
        return ucUserService.passwordLogin(req);
    }
    @ApiOperation(value = "用户-手机号注册")
    @PostMapping(path = {"register_by_mobile"}, consumes = "application/json", produces = "application/json")
    public Authorization userRegisterByMobile(@RequestBody UserMobileRegisterDto req) {
        return ucUserService.mobileRegister(req);
    }

    @ApiOperation(value = "用户-微信登录")
    @PostMapping(path = {"login_by_wechat"}, consumes = "application/json", produces = "application/json")
    public Authorization loginByWeChat(@RequestBody UserMobileRegisterDto req) {

        return ucUserService.wechatLogin(req.getAppId(),req.getWechatCode(),req.getAccessSource(),req);
    }


    @ApiOperation(value = "用户-微信解绑")
    @PostMapping(path = {"unbind_wechat"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO unbindWechat() {
        ucUserService.unbindWechat();
        return ResultVO.success();
    }

    @ApiOperation(value = "用户-微信绑定")
    @PostMapping(path = {"bind_wechat"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO bindWeChat(@RequestBody WeChatReq req) {
        ucUserService.bindWechat(req.getAppId(),req.getWechatCode());
        return ResultVO.success();
    }


    @ApiOperation(value = "获取变更手机号token")
    @PostMapping(path = {"get_change_mobile_token"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO<String> getChangeMobileToken(@RequestBody ChangeMobileDto req) {

        String token = ucUserService.getChangeMobileToken(req);
        if (StringUtils.isEmpty(token)){
            return new ResultVO<String>(ResultCode.ERROR,"");
        }
        return new ResultVO<>(token);
    }

    @ApiOperation(value = "用户-手机绑定")
    @PostMapping(path = {"bind_mobile"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO bindMobile(@RequestBody ChangeMobileDto req) {
        ucUserService.bindMobile(req);
        return ResultVO.success();
    }


}
