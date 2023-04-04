package com.tigerobo.x.pai.engine.task;

import com.tigerobo.x.pai.engine.model.NewWordModelManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Deprecated
@Slf4j
//@Component
public class NewWordTask {
    @Autowired
    private NewWordModelManager newWordModelManager;

    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;
    @Scheduled(cron = "0 0 2 * * ?")
    public void run(){

        if (!taskSwitch){
            return;
        }
        if (!supportWin){
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")){
                return;
            }
        }
        int day = 0;
        try {

            Date date = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

            Date preDay = DateUtils.addDays(date, -1);
            day = Integer.parseInt(DateFormatUtils.format(preDay, "yyyyMMdd"));
            newWordModelManager.addDay(day);
        }catch (Exception ex){
            log.error("day-err:{}",day,ex);
        }
    }

}
