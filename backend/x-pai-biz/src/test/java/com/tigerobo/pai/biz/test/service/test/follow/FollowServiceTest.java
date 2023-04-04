package com.tigerobo.pai.biz.test.service.test.follow;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class FollowServiceTest extends BaseTest {
    @Autowired
    private FollowService followService;

    @Test
    public void getFollowTabTest(){

        final List<FollowVo> followList = followService.getFollowList(3, 3);

        System.out.println(JSON.toJSONString(followList));
    }
}
