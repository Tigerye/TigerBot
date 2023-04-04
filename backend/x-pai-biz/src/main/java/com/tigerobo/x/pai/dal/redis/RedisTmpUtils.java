package com.tigerobo.x.pai.dal.redis;

import com.tigerobo.x.pai.biz.cache.RedisStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RedisTmpUtils {


    @Autowired
    private RedisStatisticService redisStatisticService;


    public Map<Object, Object> hmget(String key) {


        final Map<String, String> map = redisStatisticService.hgetAll(key);

        if (map == null){
            return null;
        }
        Map<Object, Object> oMap = new HashMap<>();

        oMap.putAll(map);
        return oMap;

    }

    public double hincr(String key, String item, double by) {

        return redisStatisticService.hincrByFloat(key,item,by);
    }

}
