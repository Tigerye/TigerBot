package com.tigerobo.x.pai.engine.auto.ml.manager;

import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.engine.auto.ml.service.AmlTrainOnlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
public class TrainServiceOnlineManager {


    @Autowired
    private AmlTrainOnlineService amlTrainOnlineService;

    @Autowired
    private AmlModelDao amlModelDao;

    public void runOnlineTask(){

        List<AmlModelDo> waitServiceOnlineList = amlModelDao.getWaitServiceOnlineList();
        if (CollectionUtils.isEmpty(waitServiceOnlineList)){
            return;
        }
        for (AmlModelDo modelDo : waitServiceOnlineList) {

            try {
                amlTrainOnlineService.dealOnline(modelDo);
            } catch (Exception e) {
                log.error("model:{}",modelDo==null?"":modelDo.getId(),e);
            }
        }

    }


    public void runOfflineTask(){

        List<AmlModelDo> waitServiceOfflineList = amlModelDao.getWaitServiceOfflineList();
        if (CollectionUtils.isEmpty(waitServiceOfflineList)){
            return;
        }
        for (AmlModelDo modelDo : waitServiceOfflineList) {

            try {
                amlTrainOnlineService.dealOffline(modelDo);
            } catch (Exception e) {
                log.error("model:{}",modelDo==null?"":modelDo.getId(),e);
            }
        }

    }

}
