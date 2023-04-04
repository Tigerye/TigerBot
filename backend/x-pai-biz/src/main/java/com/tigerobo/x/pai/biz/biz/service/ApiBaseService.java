package com.tigerobo.x.pai.biz.biz.service;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class ApiBaseService {

    @Autowired
    private ApiDao apiDao;
    @Autowired
    private RedisCacheService redisCacheService;

    public List<ApiDo> getOnlineListCache(){

        String key = "pai:mapi:online";

        final String s = redisCacheService.get(key);
        if (!StringUtils.isEmpty(s)){
            return JSON.parseArray(s,ApiDo.class);
        }
        final List<ApiDo> onlineList = apiDao.getOnlineList();


        redisCacheService.set(key, JSON.toJSONString(onlineList),3*60);

        return onlineList;
    }
}
