package com.tigerobo.pai.biz.test.service.test.pub;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.pub.BigShotQueryReq;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.biz.pub.PubSiteService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SiteServiceTest extends BaseTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private PubSiteService pubSiteService;

    @Test
    public void getPageTest(){

        ThreadLocalHolder.setUserId(3);
        BigShotQueryReq queryReq = new BigShotQueryReq();
        PageVo<FollowVo> page = pubSiteService.getPage(queryReq);

        System.out.println(page.getTotal());
        System.out.println(JSON.toJSONString(page));
    }

    @Test
    public void getListTest(){

        Integer userId = 18;
        ThreadLocalHolder.setUserId(18);
        List<PubSiteVo> list = pubSiteService.getFollowList(userId);

        for (PubSiteVo vo : list) {
            System.out.println(JSON.toJSONString(vo));
        }

    }
    @Test
    public void followTest(){

        Integer userId = 3;
        Integer bizId = 1;

        followService.follow(userId,bizId, FollowTypeEnum.SITE.getType());
    }

    @Test
    public void cancelFollowTest(){

        Integer userId = 3;
        Integer bizId = 1;

        followService.cancelFollow(userId,bizId, FollowTypeEnum.SITE.getType());
    }
}
