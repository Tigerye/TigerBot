package com.tigerobo.pai.biz.test.service.test.uc;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.dto.UserCommitSiteDto;
import com.tigerobo.x.pai.api.req.UserCommitPageReq;
import com.tigerobo.x.pai.api.req.UserCommitSiteReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.user.UserCommitService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserCommitServiceTest extends BaseTest {
    @Autowired
    private UserCommitService userCommitService;

    @Test
    public void commitTest(){

        ThreadLocalHolder.setUserId(3);
        UserCommitSiteReq dto = new UserCommitSiteReq();

        dto.setName("oss");
//        dto.setMemo("memo");
//        dto.setUrl("https://x-pai.algolet.com/biz/site/logo/2863c2f49623362e3cf5e25dc6f19df8.jpg");
        userCommitService.addCommit(dto);
    }

    @Test
    public void pageTest(){
        ThreadLocalHolder.setUserId(4);

        UserCommitPageReq reqVo = new UserCommitPageReq();
        reqVo.setUserId(18);
        PageVo<UserCommitSiteDto> userCommitPage = userCommitService.getUserCommitPage(reqVo);
        System.out.println(JSON.toJSONString(userCommitPage));

    }
}
