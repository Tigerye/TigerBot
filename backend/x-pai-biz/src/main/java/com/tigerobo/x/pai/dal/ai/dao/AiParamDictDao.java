package com.tigerobo.x.pai.dal.ai.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.dal.ai.entity.AiParamDictPo;
import com.tigerobo.x.pai.dal.ai.mapper.AiParamDictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class AiParamDictDao {

    @Autowired
    private AiParamDictMapper aiParamDictMapper;

    Cache<Integer, List<AiParamDictPo>> cache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(10)
            .build();

    public List<AiParamDictPo> getFromCache(Integer type){
        return cache.get(type,this::getByType);
    }

    public List<AiParamDictPo> getByType(Integer type){

        Example example = new Example(AiParamDictPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type",type);
        criteria.andEqualTo("isDeleted",0);

        return aiParamDictMapper.selectByExample(example);
    }
}
