package com.tigerobo.x.pai.dal.biz.dao;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.dal.biz.entity.ModelNewWordPo;
import com.tigerobo.x.pai.dal.biz.mapper.ModelNewWordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ModelNewWordDao {

    @Autowired
    private ModelNewWordMapper modelNewWordMapper;
    public void add(ModelNewWordPo po){
        if (po == null){
            return;
        }
        modelNewWordMapper.insertSelective(po);
    }
    public void update(ModelNewWordPo po){
        if (po == null){
            return;
        }
        modelNewWordMapper.updateByPrimaryKeySelective(po);
    }
    public ModelNewWordPo get(Integer day,String type){

        Example example = new Example(ModelNewWordPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("day",day);
        criteria.andEqualTo("type",type);

        List<ModelNewWordPo> list = modelNewWordMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    public List<ModelNewWordPo> search(List<Integer> days, String newsType) {
        if (CollectionUtils.isEmpty(days)|| StringUtils.isEmpty(newsType)){
            return null;
        }

        Example example = new Example(ModelNewWordPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("day",days);
        criteria.andEqualTo("type",newsType);

        example.setOrderByClause("day desc");
        PageHelper.startPage(1,20);
        return modelNewWordMapper.selectByExample(example);
    }
}
