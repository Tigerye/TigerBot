package com.tigerobo.pai.biz.test.github.service;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.dto.admin.github.GithubUserAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.req.GithubUserAdminReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.github.GithubUserQueryReq;
import com.tigerobo.x.pai.api.vo.github.GithubUserVo;
import com.tigerobo.x.pai.biz.admin.github.GithubUserAdminService;
import com.tigerobo.x.pai.biz.biz.github.GithubUserInfoService;

import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GithubUserServiceTest extends BaseTest {
    @Autowired
    private GithubUserInfoService githubUserInfoService;

    @Autowired
    private GithubUserAdminService githubUserAdminService;
    @Test
    public void listTest(){
        GithubUserAdminReq req =new GithubUserAdminReq();
        final PageVo<GithubUserAdminVo> list = githubUserAdminService.getList(req);

        System.out.println(JSON.toJSONString(list));

    }
    @Test
    public void test(){
        GithubUserQueryReq req = new GithubUserQueryReq();
        final PageVo<GithubUserVo> githubUserList = githubUserInfoService.getGithubUserList(req);


        ThreadLocalHolder.setUserId(3);
        System.out.println(JSON.toJSONString(githubUserList));

        final GithubUserVo detail = githubUserInfoService.getDetail(43830688);
        System.out.println(JSON.toJSONString(detail));
        System.out.println(detail.isFollow());
    }
}
