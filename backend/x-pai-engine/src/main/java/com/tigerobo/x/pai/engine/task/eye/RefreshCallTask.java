package com.tigerobo.x.pai.engine.task.eye;

import com.tigerobo.x.pai.biz.eye.offline.CallOfflineService;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class RefreshCallTask {

    @Autowired
    private CallOfflineService callOfflineService;


    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void refreshToday() {

        if (!taskSwitch){
            return;
        }
        if (!supportWin){
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")){
                return;
            }
        }
        long startTime = System.currentTimeMillis();
        try {
            callOfflineService.refreshSourceNum();
        }catch (Exception ex){
            log.error("refreshSourceNum",ex);
        }

        try {
            callOfflineService.refreshNum();
        }catch (Exception ex){
            log.error("refresh-aml-hupu",ex);
        }
        long delta = System.currentTimeMillis() - startTime;
        if (delta>1500){
            log.info("refresh-today,delta:{}ms",delta);
        }

    }

    @Scheduled(cron = "0 20 0 * * ?")
    public void refreshYesterday() {

        if (!taskSwitch){
            return;
        }
        if (!supportWin){
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")){
                return;
            }
        }
        int dayValue = 0;
        try {
            Date date = DateUtils.addDays(new Date(), -1);
            dayValue = TimeUtil.getDayValue(date);
        }catch (Exception ex){
            log.error("",ex);
            return;
        }

        try {
            callOfflineService.refreshSourceNum(dayValue);
        }catch (Exception ex){
            log.error("refreshSourceNum",ex);
        }

        try {
            callOfflineService.refreshNum(dayValue);
        }catch (Exception ex){
            log.error("refresh-aml-hupu",ex);
        }
    }
}
