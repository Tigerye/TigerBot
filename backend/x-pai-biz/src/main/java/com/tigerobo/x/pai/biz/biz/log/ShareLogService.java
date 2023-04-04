package com.tigerobo.x.pai.biz.biz.log;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.user.UserShareLogDao;
import com.tigerobo.x.pai.dal.biz.entity.user.UserShareLogPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShareLogService {

    @Autowired
    private UserShareLogDao userShareLogDao;
    @Autowired
    private RedisCacheService redisCacheService;

    public int getCount(String bizId,Integer type){
        if (StringUtils.isBlank(bizId)||type == null){
            return 0;
        }
        String key = getKey(bizId, type);
        return redisCacheService.getNum(key);
    }

    private String getKey(String bizId, Integer type) {
        String key = "pai:share:biz:"+ type +":"+ bizId;
        return key;
    }

    public void addLog(String bizId, Integer type, Integer sharer) {
        if (StringUtils.isBlank(bizId)||type == null||sharer == null){
            return;
        }
        Integer userId = ThreadLocalHolder.getUserId();
        String ip = ThreadLocalHolder.getIp();

        UserShareLogPo po = new UserShareLogPo();
        po.setBizId(bizId);
        po.setBizType(type);
        po.setSharer(sharer);
        po.setClicker(userId);
        po.setIp(ip);
        try {
            userShareLogDao.add(po);
            incr(bizId,type);
        } catch (Exception ex) {
            log.error("addLog,req:{}", JSON.toJSONString(po), ex);
        }
    }

    private void incr(String bizId,Integer type){
        if (StringUtils.isBlank(bizId)||type == null){
            return;
        }
        String key = getKey(bizId, type);
        redisCacheService.incr(key);
    }


}
