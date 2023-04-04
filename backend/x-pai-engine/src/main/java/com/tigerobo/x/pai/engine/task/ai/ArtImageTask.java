package com.tigerobo.x.pai.engine.task.ai;


import com.tigerobo.x.pai.biz.ai.art.image.ArtImageAiService;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageTaskService;
import com.tigerobo.x.pai.biz.notify.ArtImageNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArtImageTask {


    @Autowired
    private ArtImageAiService artImageAiService;

    @Autowired
    private ArtImageTaskService artImageTaskService;


    @Autowired
    private ArtImageNotifier artImageNotifier;
    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;



    /**
     * 数据请求算法接口
     */
    @Scheduled(cron = "0/2 * * * * ?")
    public void prepare() {

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
            artImageTaskService.dealPrepareTask(false);
        }catch (Exception ex){
            log.error("dealPrepareTask",ex);
        }
    }

    /**
     * 检测处理进度
     */
    @Scheduled(cron = "0/2 * * * * ?")
//    @Scheduled(fixedDelay=30,fixedRate = 30)
    public void modelProgress() {

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
            artImageTaskService.dealTaskResult();
        }catch (Exception ex){
            log.error("modelProgress",ex);
        }
    }


    /**
     * 钉钉通知
     */
    @Scheduled(cron = "0 */10 8-22 * * ?")
    public void queueBlockDingNotify() {

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
            artImageNotifier.blockNotifier();
        }catch (Exception ex){
            log.error("queueBlockDingNotify",ex);
        }
    }
}
