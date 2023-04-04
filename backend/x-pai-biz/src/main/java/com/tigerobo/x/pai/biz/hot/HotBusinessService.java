package com.tigerobo.x.pai.biz.hot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HotBusinessService {


    @Autowired
    private RedisCacheService redisCacheService;


    public void setBizScore(String bizId,Integer bizType,Double score){
        String sortListKey = getSortListKey(bizType);
        redisCacheService.zadd(sortListKey, score, bizId);
    }


    public List<String> getTopIdList(Integer bizType) {
        String sortListKey = getSortListKey(bizType);
        Map<String, Object> map = redisCacheService.zgetPipe(Arrays.asList(sortListKey), 0, 200);

        if (map == null) {
            return null;
        }

        Object o = map.get(sortListKey);
        if (o == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        JSONArray objects = JSON.parseArray(JSON.toJSONString(o));

        if (objects == null) {
            return null;
        }
        for (int i = 0; i < objects.size(); i++) {
            list.add(objects.getString(i));
        }

        return list;
    }

    private String getSortListKey(Integer bizType) {
        return "pai:hot:sort:biz:"+bizType;
    }

}
