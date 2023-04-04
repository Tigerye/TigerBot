package com.tigerobo.pai.admin.controller.github;

import com.tigerobo.x.pai.api.admin.auth.AdminAuthorize;
import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.GithubUserAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.req.GithubUserAdminReq;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoAdminReq;
import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.admin.github.GithubUserAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/github/user/")
@Api(value = "github组织人物", tags = "github组织人物")

public class GithubUserAdminController {
    @Autowired
    private GithubUserAdminService githubUserAdminService;

    @AdminAuthorize
    @ApiOperation(value = "组织-列表")
    @PostMapping(path = {"list"}, consumes = "application/json", produces = "application/json")
    public PageVo<GithubUserAdminVo> list(@Valid @RequestBody GithubUserAdminReq req) {
        return githubUserAdminService.getList(req);
    }

    @AdminAuthorize
    @ApiOperation(value = "更新")
    @PostMapping(path = {"update"}, produces = "application/json")
    public ResultVO updateTag(@RequestBody GithubUserDto req) {
        githubUserAdminService.update(req);
        return ResultVO.success();
    }


}
