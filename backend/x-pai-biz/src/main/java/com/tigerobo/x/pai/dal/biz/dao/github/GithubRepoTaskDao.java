package com.tigerobo.x.pai.dal.biz.dao.github;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoTaskAdminReq;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoTaskPo;
import com.tigerobo.x.pai.dal.biz.mapper.github.GithubMapper;
import com.tigerobo.x.pai.dal.biz.mapper.github.GithubRepoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

@Component
public class GithubRepoTaskDao {

    @Autowired
    private GithubRepoTaskMapper githubRepoTaskMapper;

    @Autowired
    private GithubMapper githubMapper;

    public void add(GithubRepoTaskPo po){

        githubRepoTaskMapper.insertSelective(po);
    }

    public int update(GithubRepoTaskPo po){
        return githubRepoTaskMapper.updateByPrimaryKeySelective(po);
    }

    public Page<GithubRepoTaskPo> adminQuery(RepoTaskAdminReq req) {

        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        Example example = new Example(GithubRepoTaskPo.class);
        final Example.Criteria criteria = example.createCriteria();

        if (req.getStatus()!=null){
            criteria.andEqualTo("status",req.getStatus());
        }

        if (!StringUtils.isEmpty(req.getKeyword())){
            criteria.andLike("url","%"+req.getKeyword()+"%");
        }
        example.setOrderByClause("id desc");

        return (Page<GithubRepoTaskPo>)githubRepoTaskMapper.selectByExample(example);
    }
}
