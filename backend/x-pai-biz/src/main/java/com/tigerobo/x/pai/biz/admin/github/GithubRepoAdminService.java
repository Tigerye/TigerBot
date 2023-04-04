package com.tigerobo.x.pai.biz.admin.github;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoAdminVo;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoAdminReq;
import com.tigerobo.x.pai.api.dto.github.AiTagDto;
import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.biz.biz.github.GithubUserInfoService;
import com.tigerobo.x.pai.biz.biz.github.convert.RepoConvert;
import com.tigerobo.x.pai.dal.biz.dao.github.GithubRepoInfoDao;
import com.tigerobo.x.pai.dal.biz.dao.model.ModelCategoryDao;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.model.ModelCategoryPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GithubRepoAdminService {

    @Autowired
    private GithubRepoInfoDao githubRepoInfoDao;

    @Autowired
    private GithubUserInfoService githubUserInfoService;

    @Autowired
    private ModelCategoryDao modelCategoryDao;

    public PageVo<GithubRepoAdminVo> getRepoList(RepoAdminReq repoAdminReq){
        final Page<GithubRepoInfoPo> githubRepoInfoPos = githubRepoInfoDao.adminQuery(repoAdminReq);

        final PageVo<GithubRepoAdminVo> voPage = buildPageVo(repoAdminReq, githubRepoInfoPos);
        return voPage;
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

                final List<String> aiTagUidList = vo.getAiTagUidList();

                List<AiTagDto> aiTagList = new ArrayList<>();

                if (!CollectionUtils.isEmpty(aiTagUidList)){
                    for (String aiTag : aiTagUidList) {

                        final ModelCategoryPo po = modelCategoryDao.getByUid(aiTag);

                        if (po!=null){
                            final AiTagDto aiTagDto = new AiTagDto(po.getUid(), po.getText());
                            aiTagList.add(aiTagDto);
                        }
                    }
                }

            }
        }
        return vos;
    }

    public void update(GithubRepoDto dto){

        Validate.isTrue(dto.getId()!=null&&dto.getId()>0,"id为空");
        final GithubRepoInfoPo infoPo = RepoConvert.dto2po(dto);
        final int update = githubRepoInfoDao.update(infoPo);

        Validate.isTrue(update==1,"更新失败");
    }

}
