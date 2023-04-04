package com.tigerobo.pai.biz.test.github.service;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoTaskAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoTaskAdminReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.admin.cache.AdminHolder;
import com.tigerobo.x.pai.biz.admin.github.GithubRepoTaskAdminService;
import com.tigerobo.x.pai.dal.admin.entity.SsoUserPo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GithubTaskServiceTest extends BaseTest {

    @Autowired
    private GithubRepoTaskAdminService githubRepoTaskAdminService;

    @Test
    public void addTest(){

        String url = "https://github.com/facebookresearch";

        SsoUserPo ssoUserPo = new SsoUserPo();
        ssoUserPo.setId(1);
        AdminHolder.setUser(ssoUserPo);
        githubRepoTaskAdminService.add(url);
    }

    @Test
    public void listTest(){
        RepoTaskAdminReq req = new RepoTaskAdminReq();
        final PageVo<GithubRepoTaskAdminVo> list = githubRepoTaskAdminService.getList(req);

        System.out.println(JSON.toJSONString(list));
    }
}
