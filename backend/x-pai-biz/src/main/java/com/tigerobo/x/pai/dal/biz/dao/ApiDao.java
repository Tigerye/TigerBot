package com.tigerobo.x.pai.dal.biz.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import com.tigerobo.x.pai.dal.biz.mapper.ApiMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ApiDao {

    @Autowired
    private ApiMapper apiMapper;
    Cache<Integer, List<ApiDo>> allCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(300)
            .build();

    public void insert(ApiDo apiDo){
        apiMapper.insertSelective(apiDo);
    }

    public ApiDo getByModelUuid(String modelUuid){
        return getByModelUuid(modelUuid,false);
    }
    public ApiDo getByModelUuid(String modelUuid,boolean ignoreDeleted){
        if (StringUtils.isBlank(modelUuid)){
            return null;
        }

        Example example = new Example(ApiDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uuid", modelUuid);
        if (!ignoreDeleted){
            criteria.andEqualTo("isDeleted",0);
        }

        List<ApiDo> apiDos = apiMapper.selectByExample(example);
        return CollectionUtils.isEmpty(apiDos)?null:apiDos.get(0);
    }

    public int countOnlineList(){


        Example example = new Example(ApiDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status",30);
        criteria.andEqualTo("isDeleted",0);

        return apiMapper.selectCountByExample(example);
    }

    public List<String> getRelAmlId(String uuid){
        if (StringUtils.isBlank(uuid)){
            return null;
        }
        List<ApiDo> bindAmlList = getBindAmlList();
        if (CollectionUtils.isEmpty(bindAmlList)){
            return null;
        }
        ApiDo apiDo = bindAmlList.stream().filter(b -> uuid.equals(b.getUuid())).findFirst().orElse(null);
        if (apiDo==null||StringUtils.isBlank(apiDo.getAmlRelIds())){
            return null;
        }
        return Arrays.asList(apiDo.getAmlRelIds().split(","));
    }



    public List<ApiDo> getOnlineListCache(){
        return allCache.get(1, k->getOnlineList());
    }

    public List<ApiDo> getBindAmlList(){

        List<ApiDo> onlineListCache = getOnlineListCache();
        if (CollectionUtils.isEmpty(onlineListCache)){
            return new ArrayList<>();
        }
        return onlineListCache.stream().filter(item->StringUtils.isNotBlank(item.getAmlRelIds())).collect(Collectors.toList());
    }

    public List<ApiDo> getOnlineList(){


        Example example = new Example(ApiDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status",30);
        criteria.andEqualTo("isDeleted",0);

        return apiMapper.selectByExample(example);
    }

    public void update(ApiDo update) {

        apiMapper.updateByPrimaryKeySelective(update);
    }
}
