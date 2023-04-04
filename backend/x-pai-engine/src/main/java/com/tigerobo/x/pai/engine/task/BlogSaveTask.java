package com.tigerobo.x.pai.engine.task;

import com.tigerobo.x.pai.biz.biz.blog.Twitter2BlogService;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTwitterCrawlerPo;
import com.tigerobo.x.pai.engine.manager.blog.CrawlerBlogManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
public class BlogSaveTask {

    @Autowired
    private CrawlerBlogManager crawlerBlogManager;
    @Autowired
    private Twitter2BlogService twitter2BlogService;
    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;
    @Value("${pai.task.blog.open:true}")
    boolean blogSwitch;



    /**
     * 爬虫源落库数据转存到blog表
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void site2Blog() {

        if (!canRun()) {
            return;
        }
        try {
            crawlerBlogManager.crawler2blog(false);
        } catch (Exception ex) {
            log.error("siteContent2Blog", ex);
        }
    }
    /**
     * 爬虫源落库数据处理标签分类
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void siteContentCategory() {

        if (!canRun()) {
            return;
        }
        try {
            crawlerBlogManager.recognizeCategory(false);
        } catch (Exception ex) {
            log.error("siteContent2Blog", ex);
        }
    }


    @Scheduled(cron = "0 */1 * * * ?")
    public void twitter2Blog() {
        if (!canRun()) {
            return;
        }
        try {
            save2blogTask(false);
        } catch (Exception ex) {
            log.error("siteContent2Blog", ex);
        }
    }

    public void save2blogTask(boolean test) {

        List<BlogTwitterCrawlerPo> waitDealList = twitter2BlogService.getWaitDealList(test);
        if (CollectionUtils.isEmpty(waitDealList)) {
            return;
        }

        for (BlogTwitterCrawlerPo blogTwitterCrawlerPo : waitDealList) {
            try {
                twitter2BlogService.save2blog(blogTwitterCrawlerPo);
            } catch (Exception ex) {
                log.error("thirdId:{}", blogTwitterCrawlerPo.getThirdId(), ex);
            }
        }
    }


    private boolean canRun() {
        if (!blogSwitch) {
            return false;
        }
        if (!taskSwitch) {
            return false;
        }
        if (!supportWin) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                return false;
            }
        }
        return true;
    }
}
