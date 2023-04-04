package com.tigerobo.x.pai.biz.serving;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.entity.Entity;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.utils.ThreadUtil;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.redis.RedisTmpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class ApiCountService {

    @Autowired
    protected RedisTmpUtils redisTmpUtils;

    @Autowired
    private RedisCacheService redisCacheService;

    public void incrAml(String apiKey,Integer count){
        String key = "pai:aml:model:call:"+apiKey;

        ThreadUtil.executorService.submit(()->{
            int countNum = count == null?0:count;
            redisCacheService.incr(key,countNum);
        });

    }

    public int getAmlCount(String apiKey){
        String key = "pai:aml:model:call:"+apiKey;
        return redisCacheService.getNum(key);
    }

    public void incrApiCall(ApiReqVo req) {
        ThreadUtil.executorService.submit(()->{
            try {
                Entity.Type type = Entity.Type.API;
                String uuid = req.getApiKey();
                String key = type.getModule().getName() + ":" + type.toString().toLowerCase() + ":stat:" + uuid;
                // 总量
                double cnt = redisTmpUtils.hincr(key, "call", 1);
                String item = "call";
                // 按月总量
                item = "call:" + TimeUtil.getMonthValue(new Date());
                cnt = redisTmpUtils.hincr(key, item, 1);
            }catch (Exception ex){
                log.error("incr-err:{}", JSON.toJSONString(req),ex);
            }
        });

    }

    public void incrApiCall(String apiKey){
        incrApiCall(apiKey,null);
    }
    public void incrApiCall(String apiKey,Integer count) {
        if (StringUtils.isEmpty(apiKey)){
            return;
        }
        if (count == null){
            count = 1;
        }
        try {
            Entity.Type type = Entity.Type.API;

            String key = type.getModule().getName() + ":" + type.toString().toLowerCase() + ":stat:" + apiKey;
            // 总量
            double cnt = redisTmpUtils.hincr(key, "call", count);
            String item = "call";
            // 按月总量
            item = "call:" + TimeUtil.getMonthValue(new Date());
            cnt = redisTmpUtils.hincr(key, item, count);
        }catch (Exception ex){
            log.error("incr-api-key-err:{}", apiKey,ex);
        }
    }

}
