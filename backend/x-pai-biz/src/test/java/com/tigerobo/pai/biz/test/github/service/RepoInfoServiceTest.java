package com.tigerobo.pai.biz.test.github.service;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoAdminReq;
import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.github.GithubRepoVo;
import com.tigerobo.x.pai.api.vo.github.RepoQueryReq;
import com.tigerobo.x.pai.biz.admin.github.GithubRepoAdminService;
import com.tigerobo.x.pai.biz.biz.github.GithubRepoInfoService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class RepoInfoServiceTest extends BaseTest {

    @Autowired
    private GithubRepoInfoService githubRepoInfoService;

    @Autowired
    private GithubRepoAdminService githubRepoAdminService;

    @Test
    public void adminListTest(){

        RepoAdminReq repoAdminReq = new RepoAdminReq();
//        repoAdminReq.setAiTag("machine_qa");
        repoAdminReq.setKeyword("react");
//        repoAdminReq.setIsDeleted(false);
        final PageVo<GithubRepoAdminVo> repoList = githubRepoAdminService.getRepoList(repoAdminReq);

        System.out.println(repoList.getTotal());
        System.out.println(JSON.toJSONString(repoList));
    }
    @Test
    public void updateTest(){
        GithubRepoDto dto = new GithubRepoDto();

        dto.setId(181);
        dto.setIsDeleted(true);
//        dto.setAiTagUidList(Arrays.asList("text_classification","machine_qa"));
        githubRepoAdminService.update(dto);
    }
    @Test
    public void test(){
        RepoQueryReq rep = new RepoQueryReq();
//        rep.setKeyword("Real-ESRGAN");
//        rep.setUserId(17445847);

//        ThreadLocalHolder.setUserId(3);
//        rep.setTabType("follow");
//        rep.setAiTagList(Arrays.asList("image_label","zero_classify"));
//
//        rep.setNlpUid("zero_classify");
//        rep.setCvUid("text_classification");
        final PageVo<GithubRepoVo> githubRepoVoPageVo = githubRepoInfoService.queryReq(rep);

        System.out.println(githubRepoVoPageVo.getTotal());
        System.out.println(JSON.toJSONString(githubRepoVoPageVo));
    }
    @Test
    public void followTest(){
        RepoQueryReq req = new RepoQueryReq();

//        ThreadLocalHolder.setUserId(3);

        final PageVo<GithubRepoVo> followList = githubRepoInfoService.getFollowList(req);

        System.out.println(JSON.toJSONString(followList));
    }

    @Test
    public void hotTest(){

        final List<GithubRepoDto> hotList = githubRepoInfoService.getHotList();

        System.out.println(JSON.toJSONString(hotList));
    }
}
