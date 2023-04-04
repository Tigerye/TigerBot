package com.tigerobo.x.pai.biz.biz.blog.base;

import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogBaseService {

//    @Autowired
//    private BlogInfoDao blogInfoDao;

    @Autowired
    private BlogSearchDao blogSearchDao;


    @Autowired
    private RedisCacheService redisCacheService;

    public Integer getStartId(){
        final int maxId = getMaxId();
        if (maxId>100000){
            return maxId-60000;
        }
        return null;
    }
    public int getMaxId(){

        String key = "pai:blog:maxId";
        Integer maxId = redisCacheService.getInteger(key);
        if (maxId!=null&&maxId>0){
            return maxId;
        }
        maxId = blogSearchDao.getMaxId();

        if (maxId == null){
            return 0;
        }
        redisCacheService.set(key,maxId.toString(),5*3600);
        return maxId;
    }

}
