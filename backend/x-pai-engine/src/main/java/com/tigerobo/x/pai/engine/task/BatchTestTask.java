package com.tigerobo.x.pai.engine.task;

import com.tigerobo.x.pai.biz.batch.offline.ModelBatchOfflineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchTestTask {

    @Autowired
    private ModelBatchOfflineService modelBatchOfflineService;


    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;
    @Scheduled(cron = "0/25 * * * * ?")
//    @Scheduled(fixedDelay=30,fixedRate = 30)
    public void run() {

        if (!taskSwitch){
            return;
        }
        if (!supportWin){
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")){
                return;
            }
        }

//        log.info("DatasetPrepareTask.start");
        try {
            modelBatchOfflineService.dealUnHandel(false);
        }catch (Exception ex){
            log.error("DatasetPrepareTask",ex);
        }
    }
}
