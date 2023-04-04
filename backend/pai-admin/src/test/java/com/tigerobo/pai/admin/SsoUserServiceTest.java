package com.tigerobo.pai.admin;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.admin.auth.AdminAuth;
import com.tigerobo.x.pai.api.admin.req.AdminLoginReq;
import com.tigerobo.x.pai.biz.admin.SsoUserService;
import com.tigerobo.x.pai.biz.utils.Md5Util;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SsoUserServiceTest extends AdminBaseTest {

    @Autowired
    private SsoUserService ssoUserService;


    @Test
    public void addSsoUserTest(){

        String userName = "caiwei";
        String password = "caiwei";
        Integer userId = ssoUserService.addUser(userName, password);
        assert userId!=null;

    }

    @Test
    public void loginTest(){

        String userName = "yzw";
        String password = "Tigerobo1856;";
        AdminLoginReq req = new AdminLoginReq();
        req.setUserName(userName);
        req.setPassword(password);

        AdminAuth login = ssoUserService.login(req);
        System.out.println(JSON.toJSONString(login));
    }
}
