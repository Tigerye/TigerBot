package com.tigerobo.pai.biz.test.service.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.user.UserIdPageReq;
import com.tigerobo.x.pai.api.vo.user.fan.FanVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.user.fans.FanService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FanServiceTest extends BaseTest {

    @Autowired
    private FanService fanService;

    @Autowired
    private FollowService followService;

    @Test
    public void getFollowNum(){

        final int followCountData = followService.getFollowCountData(18, null);

        System.out.println(followCountData);
    }
    @Test
    public void test(){

        ThreadLocalHolder.setUserId(3);
        final PageVo fansPage = fanService.getMyFansPage(new UserIdPageReq());

        System.out.println(JSON.toJSONString(fansPage));
    }

    @Test
    public void countTest(){

        final int i = fanService.countUserFans(18);
        System.out.println(i);
    }
}
