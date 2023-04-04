package com.tigerobo.x.pai.dal.biz.dao.blog;

import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCategoryPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCategoryRelPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogCategoryRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class BlogCategoryRelDao {


    @Autowired
    private BlogCategoryRelMapper blogCategoryRelMapper;

    public void add(BlogCategoryRelPo po){
        if (po == null){
            return;
        }
        blogCategoryRelMapper.insertSelective(po);
    }
    public List<BlogCategoryRelPo> getByBlogsWithCategoryId(List<Integer> blogIds,Integer categoryId){


        Example example = new Example(BlogCategoryRelPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("blogId",blogIds);
        criteria.andEqualTo("categoryId",categoryId);
        return blogCategoryRelMapper.selectByExample(example);
    }
    public List<BlogCategoryRelPo> getBlogRelList(int blogId){

        Example example = new Example(BlogCategoryRelPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("blogId",blogId);
        example.setOrderByClause("id asc");
        return blogCategoryRelMapper.selectByExample(example);
    }
    public List<BlogCategoryRelPo> getBlogsRelList(List<Integer> blogIds){

        Example example = new Example(BlogCategoryRelPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("blogId",blogIds);

        example.setOrderByClause("blog_id asc,id asc");
        return blogCategoryRelMapper.selectByExample(example);
    }


    public void deleteBlog(Integer blogId){
        if (blogId == null){
            return;
        }

        Example example = new Example(BlogCategoryRelPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("blogId",blogId);
        blogCategoryRelMapper.deleteByExample(example);
    }

}
