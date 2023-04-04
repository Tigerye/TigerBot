package com.tigerobo.x.pai.service.controller.github;

import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.github.GithubUserQueryReq;
import com.tigerobo.x.pai.api.vo.github.GithubUserVo;
import com.tigerobo.x.pai.biz.biz.github.GithubUserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/github/user")
@Api(value = "github用户", tags = "github用户", position = -1)
public class GithubUserController {

    @Autowired
    private GithubUserInfoService githubUserInfoService;

    @ApiOperation(value = "github用户列表")
    @PostMapping(path = "queryUserList", produces = "application/json")
    public PageVo<GithubUserVo> queryUserList(HttpServletRequest request, @Valid @RequestBody GithubUserQueryReq req) {

        final PageVo<GithubUserVo> githubUserList = githubUserInfoService.getGithubUserList(req);

        return githubUserList;
    }

    @ApiOperation(value = "github用户详情")
    @PostMapping(path = "getUserDetail", produces = "application/json")
    public GithubUserVo getRepoDetail(HttpServletRequest request, @Valid @RequestBody UserIdReq req) {

        GithubUserVo detail = githubUserInfoService.getDetail(req.getUserId());
        return detail;
    }


    @Data
    private static class UserIdReq {
        Integer userId;
    }


}
