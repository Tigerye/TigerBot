package com.tigerobo.x.pai.engine.task.aml;

import com.tigerobo.x.pai.engine.auto.ml.manager.TrainProcessManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class TrainOnProcessTask {


    @Autowired
    private TrainProcessManager trainProcessManager;

    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;
    @Scheduled(cron = "0 */2 * * * ?")
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
        try {
            trainProcessManager.run();
        }catch (Exception ex){
            log.error("TrainOnProcessTask",ex);
        }
    }
}
