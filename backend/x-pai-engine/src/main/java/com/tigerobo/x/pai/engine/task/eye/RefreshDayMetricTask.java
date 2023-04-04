package com.tigerobo.x.pai.engine.task.eye;

import com.tigerobo.x.pai.biz.eye.offline.DailyMetricOfflineService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class RefreshDayMetricTask {


    @Autowired
    private DailyMetricOfflineService dailyMetricOfflineService;
    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;

    @Scheduled(cron = "0 0/15 * * * ?")
    public void refreshToday() {

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
            dailyMetricOfflineService.addDayMetrics(new Date());
        } catch (Exception ex) {
            log.error("refreshSourceNum", ex);
        }
    }

    @Scheduled(cron = "0 40 0 * * ?")
    public void refreshYesterday() {

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
            Date date = DateUtils.addDays(new Date(), -1);
            dailyMetricOfflineService.addDayMetrics(date);
        } catch (Exception ex) {
            log.error("metric", ex);
            return;
        }
    }
}
