package com.tigerobo.pai.admin.controller.github;

import com.tigerobo.x.pai.api.admin.auth.AdminAuthorize;
import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoTaskAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoAdminReq;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoTaskAdminReq;
import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.dto.github.GithubRepoTaskDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.admin.github.GithubRepoTaskAdminService;
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
@RequestMapping("/github/task/")
public class GithubTaskController {


    @Autowired
    private GithubRepoTaskAdminService githubRepoTaskAdminService;
    @AdminAuthorize
    @ApiOperation(value = "github任务-列表")
    @PostMapping(path = {"list"}, consumes = "application/json", produces = "application/json")
    public PageVo<GithubRepoTaskAdminVo> list(@Valid @RequestBody RepoTaskAdminReq req) {
        return githubRepoTaskAdminService.getList(req);
    }

    @AdminAuthorize
    @ApiOperation(value = "添加github地址")
    @PostMapping(path = {"add"}, produces = "application/json")
    public ResultVO addAddress(@RequestBody UrlReq req) {

        final String url = req.getUrl();
        githubRepoTaskAdminService.add(url);
        return ResultVO.success();
    }
    @AdminAuthorize
    @ApiOperation(value = "更新")
    @PostMapping(path = {"update"}, produces = "application/json")
    public ResultVO updateTag(@RequestBody GithubRepoTaskDto req) {
        githubRepoTaskAdminService.update(req);
        return ResultVO.success();
    }


    @Data
    private static class UrlReq{
        String url;
    }

}

