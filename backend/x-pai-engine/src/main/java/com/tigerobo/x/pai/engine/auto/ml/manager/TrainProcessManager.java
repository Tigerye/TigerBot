package com.tigerobo.x.pai.engine.auto.ml.manager;

import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.engine.auto.ml.service.AmlTrainOnProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
public class TrainProcessManager {

    @Autowired
    private AmlTrainOnProcessService amlTrainOnProcessService;

    @Autowired
    private AmlModelDao amlModelDao;

    public void run(){

        List<AmlModelDo> onTrainProcessList = amlModelDao.getOnTrainProcessList();
        if (CollectionUtils.isEmpty(onTrainProcessList)){
            return;
        }
        log.info("aml-train-process,size:{}",onTrainProcessList.size());
        for (AmlModelDo amlModelDo : onTrainProcessList) {
            deal(amlModelDo);
        }

    }

    public void deal(AmlModelDo modelDo) {

        try {
            amlTrainOnProcessService.deal(modelDo);
        } catch (Exception e) {
            log.error("model:{}",modelDo==null?"":modelDo.getId(),e);
        }
    }

}
