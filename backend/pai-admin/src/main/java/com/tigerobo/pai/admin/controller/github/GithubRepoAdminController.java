package com.tigerobo.pai.admin.controller.github;

import com.tigerobo.x.pai.api.admin.auth.AdminAuthorize;
import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoAdminReq;
import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.admin.github.GithubRepoAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/github/repo/")
@Api(value = "github项目", tags = "github项目")

public class GithubRepoAdminController {
    @Autowired
    private GithubRepoAdminService githubRepoAdminService;

    @AdminAuthorize
    @ApiOperation(value = "项目-列表")
    @PostMapping(path = {"list"}, consumes = "application/json", produces = "application/json")
    public PageVo<GithubRepoAdminVo> list(@Valid @RequestBody RepoAdminReq req) {
        return githubRepoAdminService.getRepoList(req);
    }

    @AdminAuthorize
    @ApiOperation(value = "更新")
    @PostMapping(path = {"update"}, produces = "application/json")
    public ResultVO updateTag(@RequestBody GithubRepoDto req) {
        githubRepoAdminService.update(req);
        return ResultVO.success();
    }


}
