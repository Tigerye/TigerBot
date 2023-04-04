package com.tigerobo.pai.biz.test.service.test;

import com.tigerobo.pai.biz.test.BaseTest;
import org.junit.Test;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

public class ApolloTest extends BaseTest {

    @Resource
    Environment environment;
    @Test
    public void getBizIdTest(){

        String bizId = environment.getProperty("app.id");

        System.out.println(bizId);
    }
}
