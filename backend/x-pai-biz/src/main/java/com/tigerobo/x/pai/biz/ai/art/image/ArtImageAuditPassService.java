package com.tigerobo.x.pai.biz.ai.art.image;

import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.utils.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j
@Service
public class ArtImageAuditPassService {

    @Autowired
    private RedisCacheService redisCacheService;

    public boolean isPrePass(String text){
        if (StringUtils.isEmpty(text)){
            return false;
        }
        final String s = text.replaceAll("\\s", "");
        if (s.isEmpty()){
            return false;
        }

        final String auditKey = getAuditKey();
        final String md5 = Md5Util.getMd5(s);
        return redisCacheService.sIsMember(auditKey,md5);
    }

    public void addWord(String text){
        if (StringUtils.isEmpty(text)){
            return;
        }
        final String s = text.replaceAll("\\s", "");

        final String md5 = Md5Util.getMd5(s);

        String key = getAuditKey();
        redisCacheService.sadd(key,md5);
        redisCacheService.expire(key,10*3600);
    }

    private String getAuditKey() {
        final String day = DateFormatUtils.format(new Date(), "yyyyMMdd");

        String key = "pai:artImage:"+day;
        return key;
    }

}
