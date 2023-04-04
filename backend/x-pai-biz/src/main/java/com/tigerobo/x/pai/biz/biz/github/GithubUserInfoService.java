package com.tigerobo.x.pai.biz.biz.github;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.github.GithubUserQueryReq;
import com.tigerobo.x.pai.api.vo.github.GithubUserVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.biz.github.convert.GithubUserConvert;
import com.tigerobo.x.pai.dal.biz.dao.github.GithubUserDao;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubUserPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class GithubUserInfoService {

    @Autowired
    private GithubUserDao githubUserDao;

    @Autowired
            private FollowService followService;
    BusinessEnum businessEnum = BusinessEnum.GITHUB_USER;

    public PageVo<GithubUserVo> getGithubUserList(GithubUserQueryReq req){

        final Page<GithubUserPo> query = githubUserDao.query(req);
        PageVo<GithubUserVo> pageVo = new PageVo<>();
        pageVo.setTotal(query.getTotal());
        pageVo.setPageNum(req.getPageNum());
        pageVo.setPageSize(req.getPageSize());


        final List<GithubUserVo> vos = GithubUserConvert.po2vos(query);
        pageVo.setList(vos);

        return pageVo;
    }

    public GithubUserVo getDetail(Integer userId){

        final GithubUserPo githubUserPo = githubUserDao.loadByUserId(userId);
        final GithubUserVo vo = GithubUserConvert.po2vo(githubUserPo);

        if (vo!=null){
            final boolean follow = followService.isFollow(userId, FollowTypeEnum.GITHUB_USER.getType());
            vo.setFollow(follow);
        }

        return vo;
    }

    public GithubUserDto loadByUserId(Integer userId){

        final GithubUserPo fromCache = githubUserDao.getFromCache(userId);
        if (fromCache == null){
            return null;
        }
        GithubUserDto dto = new GithubUserDto();

        BeanUtils.copyProperties(fromCache,dto);

        return dto;
    }

    public List<Integer> searchKeyword(String keyword){

        if (StringUtils.isEmpty(keyword)){
            return null;
        }
        return githubUserDao.searchIds(keyword);
    }
}
