package com.tigerobo.x.pai.engine.task;

import com.tigerobo.x.pai.biz.crawler.CrawlerSubscribeService;
import com.tigerobo.x.pai.engine.manager.blog.SubscribeManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubscribeTask {

    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;
//    @Value("${pai.task.blog.open:true}")
//    boolean blogSwitch;

    @Autowired
    private SubscribeManager subscribeManager;

    @Scheduled(cron = "0 */1 * * * ?")
    public void run() {
        if (!canRun()) {
            return;
        }
        try {
            subscribeManager.run(false);
        } catch (Exception ex) {
            log.error("SubscribeTask", ex);
        }
    }


    private boolean canRun() {
//        if (!blogSwitch) {
//            return false;
//        }
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
