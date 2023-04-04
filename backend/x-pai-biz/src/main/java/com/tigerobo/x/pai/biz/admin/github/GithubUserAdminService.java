package com.tigerobo.x.pai.biz.admin.github;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.GithubUserAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.req.GithubUserAdminReq;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoAdminReq;
import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.github.GithubUserVo;
import com.tigerobo.x.pai.biz.biz.github.GithubUserInfoService;
import com.tigerobo.x.pai.biz.biz.github.convert.GithubUserConvert;
import com.tigerobo.x.pai.biz.biz.github.convert.RepoConvert;
import com.tigerobo.x.pai.dal.biz.dao.github.GithubRepoInfoDao;
import com.tigerobo.x.pai.dal.biz.dao.github.GithubUserDao;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubUserPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GithubUserAdminService {

    @Autowired
    private GithubUserDao githubUserDao;

    @Autowired
    private GithubUserInfoService githubUserInfoService;


    public PageVo<GithubUserAdminVo> getList(GithubUserAdminReq req){
        final Page<GithubUserPo> githubRepoInfoPos = githubUserDao.adminQuery(req);

        PageVo<GithubUserAdminVo> pageVo = new PageVo<>();
        pageVo.setTotal(githubRepoInfoPos.getTotal());
        pageVo.setPageNum(req.getPageNum());
        pageVo.setPageSize(req.getPageSize());


        final List<GithubUserAdminVo> vos = GithubUserConvert.po2adminVos(githubRepoInfoPos);
        pageVo.setList(vos);
        return pageVo;
    }

    private PageVo<GithubRepoAdminVo> buildPageVo(PageReqVo reqVo, Page<GithubRepoInfoPo> page) {
        PageVo<GithubRepoAdminVo> pageVo = new PageVo<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setPageNum(reqVo.getPageNum());
        pageVo.setPageSize(reqVo.getPageSize());
        List<GithubRepoAdminVo> vos = getGithubRepoVos(page);
        pageVo.setList(vos);
        return pageVo;
    }

    private List<GithubRepoAdminVo> getGithubRepoVos(List<GithubRepoInfoPo> page) {
        List<GithubRepoAdminVo> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(page)) {
            vos = RepoConvert.po2adminVos(page);
            for (GithubRepoAdminVo vo : vos) {
                final GithubUserDto userDto = githubUserInfoService.loadByUserId(vo.getUserId());
                vo.setGithubUser(userDto);
            }
        }
        return vos;
    }

    public void update(GithubUserDto dto){

        Validate.isTrue(dto.getId()!=null&&dto.getId()>0,"id为空");
        final GithubUserPo githubUserPo = GithubUserConvert.dto2po(dto);
        githubUserDao.update(githubUserPo);
    }

}
