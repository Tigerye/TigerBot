package com.tigerobo.x.pai.dal.biz.dao.github;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.dto.admin.github.req.GithubUserAdminReq;
import com.tigerobo.x.pai.api.vo.github.GithubUserQueryReq;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubUserPo;
import com.tigerobo.x.pai.dal.biz.mapper.github.GithubUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class GithubUserDao {
    @Autowired
    private GithubUserMapper githubUserMapper;

    Cache<Integer, GithubUserPo> userIdCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(300)
            .build();


    public List<GithubUserPo> getByThirdIds(List<Integer> ids) {
        Example example = new Example(GithubUserPo.class);
        final Example.Criteria criteria = example.createCriteria();
        criteria.andIn("thirdId", ids);
        return githubUserMapper.selectByExample(example);
    }

    public Page<GithubUserPo> query(GithubUserQueryReq req) {

        PageHelper.startPage(req.getPageNum(), req.getPageSize());

        Example example = new Example(GithubUserPo.class);
        final Example.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(req.getKeyword())) {
            criteria.andLike("userName", "%" + req.getKeyword() + "%");
        }

        criteria.andEqualTo("isDeleted", 0);

        return (Page<GithubUserPo>) githubUserMapper.selectByExample(example);
    }

    public Page<GithubUserPo> adminQuery(GithubUserAdminReq req) {

        PageHelper.startPage(req.getPageNum(), req.getPageSize());

        Example example = new Example(GithubUserPo.class);
        final Example.Criteria criteria = example.createCriteria();

        if (req.getUserId() != null && req.getUserId() > 0) {
            criteria.andEqualTo("userId", req.getUserId());
        }
        if (!StringUtils.isEmpty(req.getKeyword())) {
            criteria.andLike("userName", "%" + req.getKeyword() + "%");
        }

        if (req.getIsDeleted() != null) {
            criteria.andEqualTo("isDeleted", req.getIsDeleted());
        }


        return (Page<GithubUserPo>) githubUserMapper.selectByExample(example);
    }

    public int update(GithubUserPo po) {
        return githubUserMapper.updateByPrimaryKeySelective(po);
    }

    public int add(GithubUserPo po) {
        return githubUserMapper.insertSelective(po);
    }


    public GithubUserPo loadByUserId(Integer userId) {
        Example example = new Example(GithubUserPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);

        return githubUserMapper.selectOneByExample(example);

    }

    public GithubUserPo getFromCache(Integer userId) {
        try {
            return userIdCache.get(userId, k -> loadByUserId(userId));
        } catch (Exception ex) {
            return null;
        }
    }

    public List<Integer> searchIds(String keyword) {

        Example example = new Example(GithubUserPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andLike("userName", "%" + keyword + "%");

        criteria.andEqualTo("isDeleted", 0);

        example.selectProperties("userId");

        final List<GithubUserPo> githubUserPos = githubUserMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(githubUserPos)) {
            return null;
        }

        return githubUserPos.stream().map(p -> p.getUserId()).collect(Collectors.toList());

    }
}
