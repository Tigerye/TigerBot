package com.tigerobo.x.pai.biz.admin.github;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoTaskAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoAdminReq;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoTaskAdminReq;
import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.dto.github.GithubRepoTaskDto;
import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.biz.admin.SsoUserService;
import com.tigerobo.x.pai.biz.admin.cache.AdminHolder;
import com.tigerobo.x.pai.biz.biz.github.GithubUserInfoService;
import com.tigerobo.x.pai.biz.biz.github.convert.GithubTaskConvert;
import com.tigerobo.x.pai.biz.biz.github.convert.RepoConvert;
import com.tigerobo.x.pai.biz.biz.github.crawler.GithubCrawlerService;
import com.tigerobo.x.pai.dal.admin.entity.SsoUserPo;
import com.tigerobo.x.pai.dal.biz.dao.github.GithubRepoInfoDao;
import com.tigerobo.x.pai.dal.biz.dao.github.GithubRepoTaskDao;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoTaskPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GithubRepoTaskAdminService {

    @Autowired
    private GithubRepoTaskDao githubRepoTaskDao;

    @Autowired
    private GithubCrawlerService githubCrawlerService;
    @Autowired
    private SsoUserService ssoUserService;

    public void add(String url){
        final SsoUserPo user = AdminHolder.getUser();
        Validate.isTrue(user!=null&&user.getId()>0,"未登录");

        final Integer githubUserId = githubCrawlerService.githubUrlReq(url);
        GithubRepoTaskPo po = new GithubRepoTaskPo();
        po.setUrl(url);
        po.setType(0);
        if(githubUserId!=null&&githubUserId>0){
            po.setTargetId(String.valueOf(githubUserId));
            po.setStatus(2);
        }else {
            po.setStatus(1);
        }
        po.setSysUserId(user.getId());
        githubRepoTaskDao.add(po);
    }
    public PageVo<GithubRepoTaskAdminVo> getList(RepoTaskAdminReq req){
        final Page<GithubRepoTaskPo> githubRepoInfoPos = githubRepoTaskDao.adminQuery(req);

        final PageVo<GithubRepoTaskAdminVo> voPage = buildPageVo(req, githubRepoInfoPos);
        return voPage;
    }

    private PageVo<GithubRepoTaskAdminVo> buildPageVo(PageReqVo reqVo, Page<GithubRepoTaskPo> page) {
        PageVo<GithubRepoTaskAdminVo> pageVo = new PageVo<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setPageNum(reqVo.getPageNum());
        pageVo.setPageSize(reqVo.getPageSize());
        List<GithubRepoTaskAdminVo> vos = getGithubRepoVos(page);
        pageVo.setList(vos);
        return pageVo;
    }

    private List<GithubRepoTaskAdminVo> getGithubRepoVos(List<GithubRepoTaskPo> page) {
        List<GithubRepoTaskAdminVo> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(page)) {
            vos = GithubTaskConvert.po2adminVos(page);
        }
        return vos;
    }

    public void update(GithubRepoTaskDto dto){

        Validate.isTrue(dto.getId()!=null&&dto.getId()>0,"id为空");
        final GithubRepoTaskPo infoPo = GithubTaskConvert.dto2po(dto);
        githubRepoTaskDao.update(infoPo);
    }

}
