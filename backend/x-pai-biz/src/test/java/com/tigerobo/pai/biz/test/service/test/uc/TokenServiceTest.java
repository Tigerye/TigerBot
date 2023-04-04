package com.tigerobo.pai.biz.test.service.test.uc;


import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.user.TokenService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TokenServiceTest extends BaseTest {

    @Autowired
    private TokenService tokenService;

    @Test
    public void tokenCheckTest(){
        int id = 3;

        String token = tokenService.produceToken(id);
        Integer userId = tokenService.getUserIdByToken(token);
        Assert.assertSame(id,userId);
    }

    @Test
    public void getUserIdByTokenTest(){
        String token = "670b6b4df45a4a6a44bc10c8e0e94846";

        Integer userIdByToken = tokenService.getUserIdByToken(token);
        System.out.println(userIdByToken);


    }
}
