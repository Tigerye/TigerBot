package com.tigerobo.x.pai.dal.biz.dao.github;

import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoPo;
import com.tigerobo.x.pai.dal.biz.mapper.github.GithubRepoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class GithubRepoDao {

    @Autowired
    private GithubRepoMapper githubRepoMapper;

    public List<GithubRepoPo> getByThirdIds(List<Integer> ids){
        Example example=new Example(GithubRepoPo.class);
        final Example.Criteria criteria = example.createCriteria();
        criteria.andIn("thirdId",ids);
        return githubRepoMapper.selectByExample(example);
    }

    public int update(GithubRepoPo po) {
        return githubRepoMapper.updateByPrimaryKeySelective(po);
    }

    public int add(GithubRepoPo po) {
      return githubRepoMapper.insertSelective(po);
    }
}
