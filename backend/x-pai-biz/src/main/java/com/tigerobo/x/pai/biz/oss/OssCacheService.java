package com.tigerobo.x.pai.biz.oss;

import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OssCacheService {

    @Autowired
    private RedisCacheService redisCacheService;
    @Autowired
    private OSSHome ossHome;

    public String getPrivateKeyUrl(String key) {
        Integer timePeriod = 3600;
        return getPrivateKeyUrl(key, timePeriod);

    }

    public String getPrivateKeyUrl(String key, int timePeriod) {

        if (timePeriod < 60) {
            return null;
        }
        if (StringUtils.isBlank(key)) {
            return null;
        }
        String ossCacheKey = getOssUrlCacheKey(key);
        String cacheUrl = redisCacheService.get(ossCacheKey);
        if (StringUtils.isNotBlank(cacheUrl)) {
            return cacheUrl;
        }
        String url = ossHome.getUrlWithTime(key, timePeriod);
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String targetUrl = OssCombineUtil.getAlgoletUrl(url);
        redisCacheService.set(ossCacheKey, targetUrl, timePeriod - 5);
        return targetUrl;
    }


    private String getOssUrlCacheKey(String key) {
        return "pai:oss:c:" + key;
    }
}
