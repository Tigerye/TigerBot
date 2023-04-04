package com.tigerobo.x.pai.biz.cache.biz;

import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MachineUtil {

    @Autowired
    private RedisCacheService redisCacheService;

    volatile Integer machineId = null;

    public int getMachineId(){
        if (machineId!=null){
            return machineId;
        }

        synchronized (this){
            if (machineId==null){
                Long incr = redisCacheService.incr(getKey());
                if (incr==null){
                    log.error("getMachineId from cache error");

                    machineId = RandomUtils.getRandomInt(2);
                }else {
                    machineId = (int)(incr%100);
                    if (incr>=10000){
                        redisCacheService.expire(getKey(),1);
                    }
                }
            }
        }
        return machineId;
    }

    private String getKey(){
        return "pai:machine:index";
    }
}
