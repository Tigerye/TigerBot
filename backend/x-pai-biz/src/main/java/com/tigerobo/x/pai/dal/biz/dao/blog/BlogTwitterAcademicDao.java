package com.tigerobo.x.pai.dal.biz.dao.blog;

import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTwitterAcademicPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTwitterNoAcademicPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogTwitterAcademicMapeer;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogTwitterNoAcademicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

//@Component
public class BlogTwitterAcademicDao {
    @Autowired
    private BlogTwitterAcademicMapeer blogTwitterAcademicMapeer;
    @Autowired
    private BlogTwitterNoAcademicMapper blogTwitterNoAcademicMapper;

    public List<BlogTwitterAcademicPo> getAll(){

        List<BlogTwitterAcademicPo> blogTwitterAcademicPos = blogTwitterAcademicMapeer.selectAll();
        if (CollectionUtils.isEmpty(blogTwitterAcademicPos)){
            return new ArrayList<>();
        }
        return  blogTwitterAcademicPos;
    }
    public  List<BlogTwitterNoAcademicPo> getAllNoAcademic(){
        List<BlogTwitterNoAcademicPo> blogTwitterNoAcademicPos = blogTwitterNoAcademicMapper.selectAll();
        if(CollectionUtils.isEmpty(blogTwitterNoAcademicPos)){
            return new ArrayList<>();
        }
        return blogTwitterNoAcademicPos;
    }

    public void updateAcademicUrl(Integer id,String url){
        Example example=new Example(BlogTwitterAcademicPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",id);
        BlogTwitterAcademicPo po=new BlogTwitterAcademicPo();
        po.setImgUrl(url);
        blogTwitterAcademicMapeer.updateByExampleSelective(po,example);

    }
    public void updateNoAcademicUrl(Long id,String url){
        Example example=new Example(BlogTwitterNoAcademicPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",id);
        BlogTwitterNoAcademicPo po=new BlogTwitterNoAcademicPo();
        po.setImgUrl(url);
        blogTwitterNoAcademicMapper.updateByExampleSelective(po,example);

    }

}

