package com.tigerobo.x.pai.engine.task.aml;

import com.tigerobo.x.pai.engine.auto.ml.manager.TrainServiceOnlineManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrainServiceOnlineTask {

    @Autowired
    private TrainServiceOnlineManager trainServiceOnlineManager;

    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;
    @Scheduled(cron = "0/30 * * * * ?")
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
            trainServiceOnlineManager.runOnlineTask();
        }catch (Exception ex){
            log.error(getClass().getName()+"-online",ex);
        }
    }

    @Scheduled(cron = "*/40 * * * * ?")
    public void runOffline() {

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
            trainServiceOnlineManager.runOfflineTask();
        }catch (Exception ex){
            log.error("{}-{}",getClass().getName(),"offline",ex);
        }
    }
}
