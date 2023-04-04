package com.tigerobo.x.pai.dal.biz.dao.blog;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogCategoryQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.BlogHomeTabReq;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BloggerPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogInfoMapper;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogQueryMapper;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BloggerMapper;
import com.tigerobo.x.pai.dal.biz.mapper.pub.SiteQueryMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BlogInfoDao {

    @Autowired
    private BlogInfoMapper blogInfoMapper;

    @Autowired
    private BloggerMapper bloggerMapper;

    @Autowired
    private SiteQueryMapper siteQueryMapper;



    public int getBloggerCount(){

        Example example = new Example(BloggerPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted",0);
        return bloggerMapper.selectCountByExample(example);
    }

    public List<Integer> getTopBloggers(){

        Example example = new Example(BloggerPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("recommend",1);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("score desc");
        PageHelper.startPage(1,10);
        List<BloggerPo> list = bloggerMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.stream().map(BloggerPo::getUserId).collect(Collectors.toList());
    }

    public Page<Integer> getBloggerPageList(UserPageReq req){

        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        return (Page<Integer>)siteQueryMapper.getBloggerUserIdPage(req);
    }

    public List<Integer> getRecommendBloggers(){

        Example example = new Example(BloggerPo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("recommend",true);
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("score desc");
        PageHelper.startPage(1,10);
        List<BloggerPo> list = bloggerMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.stream().map(BloggerPo::getUserId).collect(Collectors.toList());
    }



    public BlogInfoPo load(Integer id){
        if (id == null){
            return null;
        }
        BlogInfoPo blogInfoPo = blogInfoMapper.selectByPrimaryKey(id);

        if (blogInfoPo == null){
            return null;
        }
        Boolean isDeleted = blogInfoPo.getIsDeleted();
        if (isDeleted!=null&&isDeleted){
            return null;
        }
        return blogInfoPo;
    }

    public List<BlogInfoPo> getByIds(List<Integer> ids){
        return getByIds(ids,null);
    }
    public List<BlogInfoPo> getByIds(List<Integer> ids,String orderBy){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }

        Example example = new Example(BlogInfoPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(orderBy)){
            example.setOrderByClause(orderBy);
        }
        return blogInfoMapper.selectByExample(example);
    }


    public void add(BlogInfoPo infoPo){
        if (infoPo == null){
            return;
        }

        blogInfoMapper.insertSelective(infoPo);
    }

    public void update(BlogInfoPo infoPo){
        if (infoPo ==null||infoPo.getId() == null){
            return;
        }
        int i = blogInfoMapper.updateByPrimaryKeySelective(infoPo);
        if (i!=1){
            System.out.println("id="+infoPo.getId()+",update i="+i);
        }

    }

}
