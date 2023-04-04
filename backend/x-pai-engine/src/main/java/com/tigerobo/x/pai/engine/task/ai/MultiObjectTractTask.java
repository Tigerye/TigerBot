package com.tigerobo.x.pai.engine.task.ai;



import com.tigerobo.x.pai.biz.ai.multi.object.track.AiMultiObjectTrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MultiObjectTractTask {


    @Autowired
    private AiMultiObjectTrackService aiMultiObjectTractService;


    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;

    @Scheduled(cron = "0/10 * * * * ?")
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
            aiMultiObjectTractService.dealPrepareTask(false);
        }catch (Exception ex){
            log.error("dealPrepareTask",ex);
        }
    }
}
