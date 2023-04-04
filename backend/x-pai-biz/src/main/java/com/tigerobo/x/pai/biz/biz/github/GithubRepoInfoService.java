package com.tigerobo.x.pai.biz.biz.github;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.biz.interact.InteractVo;
import com.tigerobo.x.pai.api.vo.github.GithubRepoVo;
import com.tigerobo.x.pai.api.vo.github.RepoQueryReq;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.auth.InteractionService;
import com.tigerobo.x.pai.biz.biz.github.convert.GithubConvert;
import com.tigerobo.x.pai.biz.biz.github.convert.RepoConvert;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.github.GithubRepoInfoDao;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoInfoPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GithubRepoInfoService {

    @Autowired
    private GithubRepoInfoDao githubRepoInfoDao;

    @Autowired
    private GithubUserInfoService githubUserInfoService;

    BusinessEnum businessEnum = BusinessEnum.GITHUB_REPO;

    @Autowired
    private InteractionService interactionService;

    @Autowired
    private FollowService followService;

    public List<GithubRepoVo> getGithubUserRepos(Integer userId) {

        final List<GithubRepoInfoPo> userRepoList = githubRepoInfoDao.getUserRepoList(userId);

        final List<GithubRepoVo> githubRepoVos = getGithubRepoVos(userRepoList);
        return githubRepoVos;
    }

    public List<GithubRepoDto> getHotList(){

        final List<GithubRepoInfoPo> hotList = githubRepoInfoDao.getHotList();

        return RepoConvert.po2dtos(hotList);
    }
    public PageVo<GithubRepoVo> queryReq(RepoQueryReq reqVo) {
        final String tabType = reqVo.getTabType();
        if ("follow".equalsIgnoreCase(tabType)){
            return getFollowList(reqVo);
        }
        return searchDefaultPage(reqVo);
    }

    public PageVo<GithubRepoVo> getFollowList(RepoQueryReq reqVo){
        PageVo<GithubRepoVo> defaultVo = new PageVo<>();

        final Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            return defaultVo;
        }

        final List<FollowVo> followList = followService.getFollowList(userId, Arrays.asList(FollowTypeEnum.GITHUB_USER.getType()));
        if (CollectionUtils.isEmpty(followList)){
            return defaultVo;
        }
        final List<Integer> collect = followList.stream().map(f -> f.getId()).distinct().collect(Collectors.toList());


        reqVo.setFollowUserIds(collect);

        return searchDefaultPage(reqVo);
    }
    public PageVo<GithubRepoVo> searchDefaultPage(RepoQueryReq reqVo) {
//        final List<Integer> userIds = githubUserInfoService.searchKeyword(reqVo.getKeyword());
//        final Page<GithubRepoInfoPo> page = githubRepoInfoDao.query(reqVo, userIds);

        List<String> aiTagList = new ArrayList<>();
        final String cvUid = reqVo.getCvUid();
        if (!StringUtils.isEmpty(cvUid)){
            aiTagList.add(cvUid);
        }
        if (!StringUtils.isEmpty(reqVo.getNlpUid())){
            aiTagList.add(reqVo.getNlpUid());
        }
        if (aiTagList.size()>0){
            reqVo.setAiTagList(aiTagList);
        }

        final Page<GithubRepoInfoPo> page = githubRepoInfoDao.query(reqVo);
        return buildPageVo(reqVo, page);
    }

    private PageVo<GithubRepoVo> buildPageVo(PageReqVo reqVo, Page<GithubRepoInfoPo> page) {
        PageVo<GithubRepoVo> pageVo = new PageVo<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setPageNum(reqVo.getPageNum());
        pageVo.setPageSize(reqVo.getPageSize());
        List<GithubRepoVo> vos = getGithubRepoVos(page);
        pageVo.setList(vos);
        return pageVo;
    }

    private List<GithubRepoVo> getGithubRepoVos(List<GithubRepoInfoPo> page) {
        List<GithubRepoVo> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(page)) {
            vos = RepoConvert.po2vos(page);
            for (GithubRepoVo vo : vos) {
                final GithubUserDto userDto = githubUserInfoService.loadByUserId(vo.getUserId());
                vo.setGithubUser(userDto);

                final InteractVo interactVo = interactionService.completeCount(vo.getRepoUid(), businessEnum.getType());
                vo.setInteract(interactVo);
            }
        }
        return vos;
    }

    public GithubRepoVo getDetail(String uuid) {
        final GithubRepoInfoPo infoPo = githubRepoInfoDao.loadByRepoId(uuid);
        if (infoPo == null) {
            return null;
        }
        final GithubRepoVo vo = RepoConvert.po2vo(infoPo);
        final GithubUserDto userDto = githubUserInfoService.loadByUserId(vo.getUserId());
        vo.setGithubUser(userDto);

        final InteractVo interactVo = interactionService.completeCount(infoPo.getRepoUid(), businessEnum.getType());
        vo.setInteract(interactVo);

        return vo;

    }


}
