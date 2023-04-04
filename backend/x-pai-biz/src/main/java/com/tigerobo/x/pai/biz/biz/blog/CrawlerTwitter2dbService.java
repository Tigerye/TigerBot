package com.tigerobo.x.pai.biz.biz.blog;

import com.tigerobo.x.pai.dal.biz.dao.blog.BlogTwitterCrawlerDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTwitterCrawlerPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CrawlerTwitter2dbService {

    @Autowired
    private BlogTwitterCrawlerDao blogTwitterCrawlerDao;


    public void crawler2db(List<BlogTwitterCrawlerPo> pos,boolean test){
        if (CollectionUtils.isEmpty(pos)){
            return;
        }

        List<Integer> thirdIds = pos.stream().map(po -> po.getThirdId()).collect(Collectors.toList());

        List<BlogTwitterCrawlerPo> existList = blogTwitterCrawlerDao.getByThirdIds(thirdIds);

        Map<Integer,BlogTwitterCrawlerPo> existMap = new HashMap<>();
        if (existList!=null){

            for (BlogTwitterCrawlerPo blogTwitterCrawlerPo : existList) {
                existMap.put(blogTwitterCrawlerPo.getThirdId(),blogTwitterCrawlerPo);
            }

        }

        for (BlogTwitterCrawlerPo po : pos) {
            if (test){
                po.setProcessStatus(0);
            }
            final String content = po.getContent();
            if (!StringUtils.isEmpty(content)){

                final String target = content.replaceAll("http://ipo-oss\\.aigauss\\.com", "https://ipo-oss.aigauss.com");
                po.setContent(target);
            }
            BlogTwitterCrawlerPo exist = existMap.get(po.getThirdId());
            if (exist!=null){
                po.setId(exist.getId());
                int update = blogTwitterCrawlerDao.update(po);
                if (update == 0){
                    log.error("update fail:id:{}",po.getThirdId());
                }
            }else {
                blogTwitterCrawlerDao.add(po);
            }
        }
    }


}
