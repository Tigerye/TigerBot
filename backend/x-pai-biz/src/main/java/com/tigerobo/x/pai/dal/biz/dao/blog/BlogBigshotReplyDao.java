package com.tigerobo.x.pai.dal.biz.dao.blog;

import com.tigerobo.x.pai.dal.biz.entity.blog.BlogBigshotReplyPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTranslatePo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogBigshotReplyMapper;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogTranslateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class BlogBigshotReplyDao {

    @Autowired
    private BlogBigshotReplyMapper blogBigshotReplyMapper;


    public void addList(List<BlogBigshotReplyPo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return;
        }

        for (BlogBigshotReplyPo po : pos) {
            add(po);
        }
    }

    public void add(BlogBigshotReplyPo po){
        blogBigshotReplyMapper.insertSelective(po);
    }


    public List<BlogBigshotReplyPo> getByBlogId(Integer blogId){

        Example example = new Example(BlogBigshotReplyPo.class);
        example.selectProperties("specId");

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("blogId",blogId);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("publish_time");
        return blogBigshotReplyMapper.selectByExample(example);
    }

    public void update(BlogBigshotReplyPo po){
        blogBigshotReplyMapper.updateByPrimaryKeySelective(po);
    }


    public void delete(Integer blogId){

        Example example = new Example(BlogBigshotReplyPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("blogId",blogId);

        blogBigshotReplyMapper.deleteByExample(example);

    }
}
