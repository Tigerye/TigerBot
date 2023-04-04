package com.tigerobo.x.pai.engine.auto.ml.manager;

import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.engine.auto.ml.service.AmlStartTrainService;
import com.tigerobo.x.pai.engine.exception.AmlException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class StartTrainManager {

    @Autowired
    private AmlStartTrainService amlStartTrainService;

    @Value("${engine.lake.task.max:5}")
    int lakeMaxCount;
//
//    @PostConstruct
//    public void init(){
//        log.info("StartTrainManager,lakeMaxCount{}",lakeMaxCount);
//    }
    public void run(){
        List<AmlModelDo> waitDealList = amlStartTrainService.getWaitDealList();
        if (CollectionUtils.isEmpty(waitDealList)){
            return;
        }
        int processCount = amlStartTrainService.countOnProcess();
        if (processCount>=lakeMaxCount){
            log.warn("当前处理中的为{}，大于极限任务数:{}",processCount,lakeMaxCount);
            return;
        }

        for (AmlModelDo modelDo : waitDealList) {
            try {
                processCount = amlStartTrainService.countOnProcess();
                if (processCount>=lakeMaxCount){
                    log.warn("sub-当前处理中的为{}，大于极限任务数:{}",processCount,lakeMaxCount);
                    return;
                }
                deal(modelDo);
            }catch (Exception ex){
                log.error("modelId:{}",modelDo==null?"":modelDo.getId(),ex);
            }
        }

    }

    private void deal(AmlModelDo modelDo){
        if (modelDo == null){
            return;
        }
        String err = null;
        try {
            amlStartTrainService.process(modelDo);
        }catch (IllegalArgumentException| AmlException ex){
            log.error("model:{}",modelDo.getId(),ex);
            err = ex.getMessage();
        }catch (FileNotFoundException ex){
            log.error("model:{}",modelDo.getId(),ex);
            err = "文件不存在";
        }catch (IOException ex){
            err = "文件处理异常";
            log.error("model:{}",modelDo.getId(),ex);
        }catch (Exception ex){
            err = "服务异常";
            log.error("model:{}",modelDo.getId(),ex);
        }

        if (!StringUtils.isEmpty(err)){
            try {
                amlStartTrainService.updateTrainFail(modelDo,err);
            }catch (Exception subEx){
                log.error("updateFail,modelId:{}",modelDo.getId(),subEx);
            }
        }
    }
}
