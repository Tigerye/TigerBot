package com.tigerobo.x.pai.dal.biz.dao.github;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.dto.admin.github.req.RepoAdminReq;
import com.tigerobo.x.pai.api.vo.github.RepoQueryReq;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoPo;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubUserPo;
import com.tigerobo.x.pai.dal.biz.mapper.github.GithubMapper;
import com.tigerobo.x.pai.dal.biz.mapper.github.GithubRepoInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class GithubRepoInfoDao {

    @Autowired
    private GithubRepoInfoMapper githubRepoInfoMapper;

    @Autowired
    private GithubMapper githubMapper;

    public int update(GithubRepoInfoPo po) {
        return githubRepoInfoMapper.updateByPrimaryKeySelective(po);
    }

    public int add(GithubRepoInfoPo po) {
      return githubRepoInfoMapper.insertSelective(po);
    }

    public GithubRepoInfoPo load(int id){

        return githubRepoInfoMapper.selectByPrimaryKey(id);

    }

    public List<GithubRepoInfoPo> getUserRepoList(Integer userId){
        Example example = new Example(GithubRepoInfoPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("isDeleted",0);

        return githubRepoInfoMapper.selectByExample(example);
    }



    public List<GithubRepoInfoPo> getHotList(){
        PageHelper.startPage(1,10);
        Example example = new Example(GithubRepoPo.class);
        example.setOrderByClause("star_count desc");
        return githubRepoInfoMapper.selectByExample(example);
    }


    public Page<GithubRepoInfoPo> query(RepoQueryReq req){
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        final Page<Integer> list = (Page<Integer>)githubMapper.queryRepoIds(req);

        Page<GithubRepoInfoPo> poPage = new Page<>();

        poPage.setTotal(list.getTotal());

        if (list.size()>0){
            final List<GithubRepoInfoPo> pos = getByIds(list);
            if (!CollectionUtils.isEmpty(pos)){
                poPage.addAll(pos);
            }
        }
        return poPage;
    }



    public Page<GithubRepoInfoPo> adminQuery(RepoAdminReq req){
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        final Page<Integer> list = (Page<Integer>)githubMapper.adminQueryRepoIds(req);

        Page<GithubRepoInfoPo> poPage = new Page<>();
        poPage.setTotal(list.getTotal());

        if (list.size()>0){
            final List<GithubRepoInfoPo> pos = getByIds(list);
            if (!CollectionUtils.isEmpty(pos)){
                poPage.addAll(pos);
            }
        }
        return poPage;
    }

    private List<GithubRepoInfoPo> getByIds(List<Integer> ids){

        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        Example example = new Example(GithubRepoInfoPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);

        example.setOrderByClause("id desc");

        return githubRepoInfoMapper.selectByExample(example);
    }

    public Page<GithubRepoInfoPo> query(RepoQueryReq req, List<Integer> userIds){


        Example example = new Example(GithubRepoInfoPo.class);

        if (StringUtils.isNotBlank(req.getKeyword())){
            final Example.Criteria criteria = example.createCriteria();
            final Example.Criteria or = example.or();
            or.orLike("reposName","%"+req.getKeyword()+"%");
            if (userIds!=null&&userIds.size()>0){
                or.orIn("userId",userIds);
            }
        }


        example.setOrderByClause("id desc");
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        return (Page<GithubRepoInfoPo>)githubRepoInfoMapper.selectByExample(example);

    }

    public GithubRepoInfoPo loadByRepoId(String repoUid){

        Example example = new Example(GithubRepoInfoPo.class);

        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("repoUid",repoUid);

        return githubRepoInfoMapper.selectOneByExample(example);
    }

}

