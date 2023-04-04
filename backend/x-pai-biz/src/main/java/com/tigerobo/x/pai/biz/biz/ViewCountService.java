package com.tigerobo.x.pai.biz.biz;

import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.constant.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ViewCountService {

    @Autowired
    private RedisCacheService redisCacheService;

    public void incrView(String bizId, Integer bizType) {
        if (StringUtils.isBlank(bizId)) {
            return;
        }
        try {
            String key = RedisConstants.getBizViewKey(bizType,bizId);
            Long incr = redisCacheService.incr(key);

        } catch (Exception ex) {
            log.error("incrView,id={},bizType:{}", bizId,bizType);
            //todo,按人记表
        }
    }

    public int getView(Integer bizType,String bizId) {
        String key = RedisConstants.getBizViewKey(bizType,bizId);
        return redisCacheService.getNum(key);
    }


}
