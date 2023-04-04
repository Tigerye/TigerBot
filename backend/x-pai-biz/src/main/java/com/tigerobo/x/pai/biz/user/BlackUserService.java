package com.tigerobo.x.pai.biz.user;

import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlackUserService {

    @Autowired
    private RedisCacheService redisCacheService;

    String key = "user:blackList";
    public boolean isBlackUser(Integer userId){
        if (userId == null){
            return false;
        }
        return redisCacheService.sIsMember(key, userId.toString());
    }

    public void addBlackUser(Integer userId){
        if (userId == null){
            return;
        }
        redisCacheService.sadd(key,userId.toString());
    }

    public void removeBlackUser(Integer userId){
        if (userId == null){
            return;
        }
        redisCacheService.srem(key,userId.toString());
    }

    public List<Integer> getBlackUserIds(){

        final Set<String> smembers = redisCacheService.smembers(key);

        if (CollectionUtils.isEmpty(smembers)){
            return null;
        }
        return smembers.stream().map(s->Integer.parseInt(s)).collect(Collectors.toList());
    }
}
