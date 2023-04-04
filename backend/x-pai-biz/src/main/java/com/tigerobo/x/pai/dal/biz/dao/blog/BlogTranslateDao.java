package com.tigerobo.x.pai.dal.biz.dao.blog;

import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTranslatePo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogTranslateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

@Component
public class BlogTranslateDao {

    @Autowired
    private BlogTranslateMapper blogTranslateMapper;

    public void addOrUpdate(BlogTranslatePo po){

        BlogTranslatePo exist = getByKey(po.getThirdId());
        if (exist!=null){
            po.setId(exist.getId());
            update(po);
        }else {
           add(po);
        }
    }

    public void add(BlogTranslatePo po){
        blogTranslateMapper.insertSelective(po);
    }

    public BlogTranslatePo getByKey(Integer thirdId){

        Example example = new Example(BlogTranslatePo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("thirdId",thirdId);
        return blogTranslateMapper.selectOneByExample(example);
    }

    public void update(BlogTranslatePo po){
        blogTranslateMapper.updateByPrimaryKeySelective(po);
    }
}
