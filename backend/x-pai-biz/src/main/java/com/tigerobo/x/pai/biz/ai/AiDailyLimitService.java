package com.tigerobo.x.pai.biz.ai;

import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.dal.ai.dao.AiDailyLimitDao;
import com.tigerobo.x.pai.dal.ai.entity.AiDailyLimitPo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiDailyLimitService {


    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private AiDailyLimitDao aiDailyLimitDao;

    public Integer getUserDailyLimit(Integer userId){
        if (userId == null){
            return 0;
        }

        final String key = getKey();
        final String value = redisCacheService.hget(key, userId.toString());
        if (StringUtils.isBlank(value)){
            return null;
        }
        if (!value.matches("\\d+")){
            return null;
        }
        final int i = Integer.parseInt(value);
        if (i<0){
            return null;
        }
        return i;
    }

    public void setUserCount(Integer userId,Integer num){
        if (userId == null){
            return;
        }
        if (num == null){
            num = -1;
        }

        final AiDailyLimitPo dbPo = aiDailyLimitDao.loadByUserId(userId);
        AiDailyLimitPo opt = new AiDailyLimitPo();
        opt.setUserId(userId);
        opt.setNum(num);
        if (dbPo == null){
            aiDailyLimitDao.add(opt);
        }else {
            opt.setId(dbPo.getId());
            aiDailyLimitDao.update(opt);
        }
        final String key = getKey();
        redisCacheService.hset(key,userId.toString(),num.toString());
    }

    private String getKey(){
        return "pai:ai:daily:limit";
    }

    @Data
    private class LimitCache{
        private Integer num;
        private Integer expire;
    }
}
