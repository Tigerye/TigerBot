package com.tigerobo.x.pai.engine.task.ai;


import com.tigerobo.x.pai.biz.ai.photo.PhotoFixAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PhotoTask {


    @Autowired
    private PhotoFixAiService photoFixAiService;


    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;

    @Scheduled(cron = "0/20 * * * * ?")
//    @Scheduled(fixedDelay=30,fixedRate = 30)
    public void process() {
        if (!taskSwitch) {
            return;
        }
        if (!supportWin) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                return;
            }
        }
        try {
            photoFixAiService.dealWaitDealList(false);
        } catch (Exception ex) {
            log.error("modelProgress", ex);
        }
    }

}
