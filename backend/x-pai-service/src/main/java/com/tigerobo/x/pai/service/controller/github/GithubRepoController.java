package com.tigerobo.x.pai.service.controller.github;

import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.github.GithubRepoVo;
import com.tigerobo.x.pai.api.vo.github.RepoQueryReq;
import com.tigerobo.x.pai.biz.biz.github.GithubRepoInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/github/repo")
@Api(value = "github仓库", tags = "github仓库", position = -1)
public class GithubRepoController {

    @Autowired
    private GithubRepoInfoService githubRepoInfoService;

    @ApiOperation(value = "github仓库列表")
    @PostMapping(path = "queryRepoList", produces = "application/json")
    public PageVo<GithubRepoVo> queryRepoList(HttpServletRequest request, @Valid @RequestBody RepoQueryReq req) {

        final PageVo<GithubRepoVo> githubRepoVoPageVo = githubRepoInfoService.queryReq(req);

        return githubRepoVoPageVo;
    }

    @ApiOperation(value = "github热门列表")
    @PostMapping(path = "getHotRepoList", produces = "application/json")
    public List<GithubRepoDto> getHotRepoList(HttpServletRequest request) {

        List<GithubRepoDto> hotList = githubRepoInfoService.getHotList();
        if (hotList == null){
            hotList = Lists.newArrayList();
        }
        return hotList;
    }

    @ApiOperation(value = "github用户仓库列表")
    @PostMapping(path = "getUserRepoList", produces = "application/json")
    public List<GithubRepoVo> getUserRepoList(HttpServletRequest request, @Valid @RequestBody GithubUserReq req) {

        final List<GithubRepoVo> githubRepoVoPageVo = githubRepoInfoService.getGithubUserRepos(req.getGithubUserId());

        return githubRepoVoPageVo;
    }

    @ApiOperation(value = "github仓库详情")
    @PostMapping(path = "getRepoDetail", produces = "application/json")
    public GithubRepoVo getRepoDetail(HttpServletRequest request, @Valid @RequestBody RepoIdReq req) {
        return githubRepoInfoService.getDetail(req.getRepoUid());
    }


    @Data
    private static class RepoIdReq {
        String repoUid;
    }

    @Data
    private static class GithubUserReq {
        Integer githubUserId;
    }
}
