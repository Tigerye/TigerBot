package com.tigerobo.x.pai.dal.biz.dao.model;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import com.tigerobo.x.pai.dal.biz.entity.model.ModelCategoryPo;
import com.tigerobo.x.pai.dal.biz.mapper.model.ModelCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ModelCategoryDao {
    Cache<String, ModelCategoryPo> cache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(300)
            .build();

    @Autowired
    private ModelCategoryMapper modelCategoryMapper;

    public ModelCategoryPo getByUid(String uid){
        Example example = new Example(ModelCategoryPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uid",uid);
        criteria.andEqualTo("isDeleted",0);
        List<ModelCategoryPo> modelCategoryPos = modelCategoryMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(modelCategoryPos)){
            return null;
        }
        return modelCategoryPos.get(0);
    }

    public ModelCategoryPo getByUidFromCache(String uid){
        if (StringUtils.isEmpty(uid)){
            return null;
        }
        try {
            return cache.get(uid, this::getByUid);
        }catch (Exception ex){
            return null;
        }
    }

    public List<ModelCategoryPo> getAll(){
        Example example = new Example(ModelCategoryPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted",0);

        return modelCategoryMapper.selectByExample(example);
    }


    public List<ModelCategoryPo> getByType(Integer type){

        Example example = new Example(ModelCategoryPo.class);

        Example.Criteria criteria = example.createCriteria();
        if (type !=null){
            criteria.andEqualTo("type",type);
        }
        criteria.andEqualTo("isDeleted",0);

        return modelCategoryMapper.selectByExample(example);
    }
}
