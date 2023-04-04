package com.tigerobo.x.pai.dal.biz.dao.blog;

import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCategoryPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogCategoryMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BlogCategoryDao {


    @Autowired
    private BlogCategoryMapper blogCategoryMapper;

    public List<BlogCategoryPo> getAll(){
        List<BlogCategoryPo> blogCategoryPos = blogCategoryMapper.selectAll();
        if (CollectionUtils.isEmpty(blogCategoryPos)){
            return null;
        }

        return blogCategoryPos.stream().filter(b->b.getIsDeleted()!=null&&!b.getIsDeleted()).collect(Collectors.toList());
    }
}
