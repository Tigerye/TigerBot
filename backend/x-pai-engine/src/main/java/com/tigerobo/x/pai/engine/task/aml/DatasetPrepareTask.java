package com.tigerobo.x.pai.engine.task.aml;

import com.tigerobo.x.pai.engine.auto.ml.manager.DatasetPrepareManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DatasetPrepareTask {

    @Autowired
    private DatasetPrepareManager datasetPrepareManager;

    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;
    @Scheduled(cron = "0/30 * * * * ?")
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
            datasetPrepareManager.dealUnhandled(false);
        }catch (Exception ex){
            log.error("DatasetPrepareTask",ex);
        }
    }
}
