package com.tigerobo.pai.admin.controller;

import com.tigerobo.x.pai.api.admin.auth.AdminAuth;
import com.tigerobo.x.pai.api.admin.req.AdminLoginReq;
import com.tigerobo.x.pai.biz.admin.SsoUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping(value = "/auth")
@Api(value = "用户授权",  tags = "用户授权")
public class AdminAuthController {

    @Autowired
    private SsoUserService ssoUserService;
    @ApiOperation(value = "后台用户登录")
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AdminAuth createAuthenticationToken(@RequestBody AdminLoginReq req) {
        return ssoUserService.login(req);
    }

    @ApiOperation(value = "token校验")
    @ResponseBody
    @RequestMapping(value = "/tokencheck", method = RequestMethod.POST)
    public Boolean checkTokenVa(@RequestBody String token){
        return ssoUserService.checkToken(token);

    }


}
