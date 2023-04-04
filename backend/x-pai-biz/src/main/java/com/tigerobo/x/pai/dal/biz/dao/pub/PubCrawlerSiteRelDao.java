package com.tigerobo.x.pai.dal.biz.dao.pub;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubCrawlerSiteRelPo;
import com.tigerobo.x.pai.dal.biz.mapper.PubCrawlerSiteRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class PubCrawlerSiteRelDao {

    @Autowired
    private PubCrawlerSiteRelMapper pubCrawlerSiteRelMapper;

    Cache<Integer, List<PubCrawlerSiteRelPo>> localCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(300)
            .build();

    public List<PubCrawlerSiteRelPo> getAll(){
        return pubCrawlerSiteRelMapper.selectAll();
    }


    private List<PubCrawlerSiteRelPo> getFromCache(){
        return localCache.get(0,k->getAll());
    }

    public Integer getSiteIdBySrcId(Integer srcId){
        if (srcId==null||srcId==0){
            return null;
        }
        List<PubCrawlerSiteRelPo> list = getFromCache();
        if (CollectionUtils.isEmpty(list)){
            return null;
        }

        Optional<PubCrawlerSiteRelPo> first = list.stream().filter(item -> srcId.equals(item.getSrcId())).findFirst();

        return first.map(PubCrawlerSiteRelPo::getSiteId).orElse(null);
    }
}
