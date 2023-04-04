package com.tigerobo.pai.biz.test.service.test.uc;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.user.BlackUserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserBlackServiceTest extends BaseTest {

    @Autowired
    private BlackUserService blackUserService;

    @Test
    public void setBlackTest(){

        blackUserService.addBlackUser(3909);
//        blackUserService.removeBlackUser(47);
    }
}
