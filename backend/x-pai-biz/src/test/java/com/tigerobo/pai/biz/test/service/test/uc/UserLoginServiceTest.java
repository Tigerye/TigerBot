package com.tigerobo.pai.biz.test.service.test.uc;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.auth.entity.Authorization;
import com.tigerobo.x.pai.api.uc.dto.UserMobileRegisterDto;
import com.tigerobo.x.pai.biz.user.UcUserServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserLoginServiceTest extends BaseTest {

    @Autowired
    private UcUserServiceImpl ucUserService;


    @Test
    public void registerTest() {
        String json = "{\"mobile\":\"18301111195\",\"password\":\"Wsen1192@\",\"nikeName\":\"nick\",\"code\":\"0726\",\"area\":\"+86\"}";


        UserMobileRegisterDto registerDto = JSON.parseObject(json, UserMobileRegisterDto.class);
        Authorization authorization = ucUserService.mobileRegister(registerDto);
        System.out.println(authorization);
    }

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void mobileLoginTest() throws Exception {
        UserMobileRegisterDto registerDto = new UserMobileRegisterDto();
        registerDto.setMobile("18301966691");
        registerDto.setCode("0726");
        Authorization authorization = ucUserService.mobileCodeLogin(registerDto);


        System.out.println(objectMapper.writeValueAsString(authorization));
    }
}
