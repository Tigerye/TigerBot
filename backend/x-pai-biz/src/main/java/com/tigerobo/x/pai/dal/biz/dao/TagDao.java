package com.tigerobo.x.pai.dal.biz.dao;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.dal.biz.entity.TagPo;
import com.tigerobo.x.pai.dal.biz.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class TagDao {

    @Autowired
    private TagMapper tagMapper;

    public TagPo getByUid(String uid){
        if (StringUtils.isEmpty(uid)){
            return null;
        }
        Example example = new Example(TagPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uid",uid);
        return tagMapper.selectOneByExample(example);
    }
    public List<TagPo> getByUids(List<String> uids){
        if (CollectionUtils.isEmpty(uids)){
            return null;
        }
        Example example = new Example(TagPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("uid",uids);
        return tagMapper.selectByExample(example);
    }

    public List<TagPo> getByType(Integer type){

        Example example = new Example(TagPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type",type);

        return tagMapper.selectByExample(example);
    }


    public List<TagPo> getAll(){

        Example example = new Example(TagPo.class);
        PageHelper.startPage(1,500);
        return tagMapper.selectByExample(example);
    }

    public void insert(TagPo tagPo){

        if (tagPo == null){
            return;
        }

        tagMapper.insertSelective(tagPo);
    }
    public void insertList(List<TagPo> list){

        if (list == null||list.isEmpty()){
            return;
        }

        tagMapper.insertList(list);
    }


}
