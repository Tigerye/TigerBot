package com.tigerobo.x.pai.dal.aml.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.tigerobo.x.pai.dal.aml.entity.AmlBaseModelDo;
import com.tigerobo.x.pai.dal.aml.mapper.AmlBaseModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class AmlBaseModelDao {

    @Autowired
    private AmlBaseModelMapper amlBaseModelMapper;
    Cache<Integer, AmlBaseModelDo> cache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .initialCapacity(10)
            .maximumSize(100L)
            .build();

    public List<AmlBaseModelDo> getAll() {

        Example example = new Example(AmlBaseModelDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted", 0);

        return amlBaseModelMapper.selectByExample(example);
    }

    public AmlBaseModelDo getById(Integer id) {
        return amlBaseModelMapper.selectByPrimaryKey(id);
    }


    public AmlBaseModelDo getFromCache(Integer id) {
        if (id == null){
            return null;
        }
        return cache.get(id,k->getById(k));
    }
}
