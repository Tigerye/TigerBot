package com.tigerobo.x.pai.engine.manager.blog;

import com.tigerobo.x.pai.api.enums.ProcessStatusEnum;
import com.tigerobo.x.pai.biz.crawler.CrawlerSubscribeService;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubBigShotDao;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SubscribeManager {

    @Autowired
    private PubBigShotDao pubBigShotDao;

    @Autowired
    private CrawlerSubscribeService crawlerSubscribeService;

    public void run(boolean test){

        List<PubBigShotPo> waitDealList;

        if (test){
            waitDealList = pubBigShotDao.getTestWaitDealList();
        }else {
            waitDealList = pubBigShotDao.getWaitDealList();
        }

        for (PubBigShotPo po : waitDealList) {
            try {
                process(po);
            }catch (Exception ex){
                log.error("id:{}",po.getId(),ex);
            }
        }
    }

    private void process(PubBigShotPo po){
        String webUrl = po.getWebUrl();

        Integer srcId = crawlerSubscribeService.subscribe(webUrl);

        boolean success = srcId!=null&&srcId>0;

        PubBigShotPo update = new PubBigShotPo();
        update.setId(po.getId());
        if (success){
            update.setSubscribeStatus(ProcessStatusEnum.SUCCESS.getStatus());
            update.setSrcId(srcId);
            pubBigShotDao.update(update);
        }else {
            update.setSubscribeStatus(ProcessStatusEnum.FAIL.getStatus());
            update.setErrMsg("调用爬虫订阅失败");
            pubBigShotDao.update(update);
        }
    }
}
