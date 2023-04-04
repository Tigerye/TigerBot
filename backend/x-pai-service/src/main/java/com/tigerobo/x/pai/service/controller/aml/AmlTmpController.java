package com.tigerobo.x.pai.service.controller.aml;

import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.dal.aml.dao.AmlInfoDao;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlInfoDo;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/aml/tmp")
public class AmlTmpController {

    @Autowired
    private AmlModelDao amlModelDao;
    @Autowired
    private AmlInfoDao amlInfoDao;
    @GetMapping("/resetTrain")
    public String resetModelTrain(Integer amlId,String key){

        if ("Tigerobo0726".equals(key)){
            AmlInfoDo amlInfoDo = amlInfoDao.loadById(amlId);
            if (amlInfoDo ==null){
                return "训练不存在";
            }
            Integer currentModelId = amlInfoDo.getCurrentModelId();
            if (currentModelId == null||currentModelId==0){
                return "没有开始训练的模型";
            }
            AmlModelDo model = amlModelDao.getById(currentModelId);
            if (model==null){
                return "模型不存在";
            }
            if (AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(model.getStatus())||AmlStatusEnum.TRAIN_FAIL.getStatus().equals(model.getStatus())){
                AmlModelDo update = new AmlModelDo();
                update.setStatus(AmlStatusEnum.WAIT_TRAIN.getStatus());
                update.setId(currentModelId);
                update.setErrMsg("");
                update.setEpoch(0);
                update.setTotalEpoch(0);
                amlModelDao.update(update);
                AmlInfoDo infoDo = new AmlInfoDo();
                infoDo.setId(amlId);
                infoDo.setStatus(AmlStatusEnum.WAIT_TRAIN.getStatus());
                amlInfoDao.updateByPrimaryKey(infoDo);
            }else {
                return "当前模型不能更改状态";
            }
            return "成功";
        }else {
            return "验证key失败";
        }
    }
}
