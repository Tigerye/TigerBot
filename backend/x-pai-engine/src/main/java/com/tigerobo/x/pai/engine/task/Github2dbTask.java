package com.tigerobo.x.pai.engine.task;

import com.tigerobo.x.pai.biz.biz.blog.Twitter2BlogService;
import com.tigerobo.x.pai.engine.manager.blog.CrawlerBlogManager;
import com.tigerobo.x.pai.engine.manager.github.CrawlerGithubRepoManger;
import com.tigerobo.x.pai.engine.manager.github.CrawlerGithubUserManger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Github2dbTask {
    @Autowired
    private CrawlerGithubRepoManger crawlerGithubRepoManger;

    @Autowired
    private CrawlerGithubUserManger crawlerGithubUserManger;


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



    @Scheduled(cron = "0 0 0/1 * * ?")
    public void githubRepo2db() {
        if (!canRun()) {
            return;
        }
        try {
            crawlerGithubRepoManger.dealCrawlerRepoData();
        } catch (Exception e) {
            log.error("prepareGithubRepo", e);
        }
    }

    @Scheduled(cron = "0 5 0/1 * * ?")
    public void githubUser2db() {
        if (!canRun()) {
            return;
        }
        try {
            crawlerGithubUserManger.dealCrawlerRepoData();
        } catch (Exception e) {
            log.error("prepareGithubUser", e);
        }
    }



    private boolean canRun() {
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
