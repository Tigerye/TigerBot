package com.tigerobo.x.pai.engine.task;

import com.tigerobo.x.pai.engine.manager.blog.CrawlerBlogManager;
import com.tigerobo.x.pai.engine.manager.blog.CrawlerTwitter2dbManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CrawlerBlog2dbTask {

    @Autowired
    private CrawlerBlogManager crawlerBlogManager;
    @Autowired
    private CrawlerTwitter2dbManager crawlerTwitter2dbManager;

    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;
    @Value("${pai.task.blog.open:true}")
    boolean blogSwitch;


    /**
     * 爬虫源落库
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void site2db() {
        if (!canRun()) {
            return;
        }
        try {
            crawlerBlogManager.saveCrawler2db();
        } catch (Exception ex) {
            log.error("prepareSiteContent", ex);
        }
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void twitter2db() {
        if (!canRun()) {
            return;
        }
        try {
            crawlerTwitter2dbManager.dealCrawlerTwitterData();
        } catch (Exception ex) {
            log.error("prepareTwitter", ex);
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
