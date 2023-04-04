package com.tigerobo.x.pai.biz.biz.service;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.vo.biz.ModelApiReq;
import com.tigerobo.x.pai.biz.biz.process.ImageFactory;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.dao.ModelDao;
import com.tigerobo.x.pai.dal.biz.dao.TaskDao;
import com.tigerobo.x.pai.dal.biz.dao.TaskModelDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import com.tigerobo.x.pai.dal.biz.entity.ModelDo;
import com.tigerobo.x.pai.dal.biz.entity.TaskDo;
import com.tigerobo.x.pai.dal.biz.entity.TaskModelDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class ModelAppService {

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private ModelDao modelDao;


    @Autowired
    private ApiDao apiDao;

    @Autowired
    private ImageFactory imageFactory;

    @Autowired
    private TaskModelDao taskModelDao;

    public void addApp(ModelApiReq req){
        Preconditions.checkArgument(StringUtils.isNotBlank(req.getModelName()),"modelName为空");
//        Preconditions.checkArgument(StringUtils.isNotBlank(req.getApiUrl()),"apiUrl为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(req.getAppShortName()),"appShortName为空");

        String modelUuid = req.getModelUuid();

        if (StringUtils.isBlank(modelUuid)){
            modelUuid = IdGenerator.getId();
        }

        ModelDo modelDo = addModel(req, modelUuid);

        addApi(req, modelUuid, modelDo);
//        String image = imageFactory.getImage();
        TaskDo taskDo = addTask(req, modelDo);


        TaskModelDo taskModelDo = new TaskModelDo();

        taskModelDo.setCreateBy(String.valueOf(req.getUserId()));
        taskModelDo.setUpdateBy("");
        taskModelDo.setModelUuid(modelDo.getUuid());
        taskModelDo.setModelId(modelDo.getId());
        taskModelDo.setTaskUuid(taskDo.getUuid());
        taskModelDo.setTaskId(taskDo.getId());
        taskModelDo.setUuid(modelUuid);
        taskModelDao.insert(taskModelDo);
    }

    private void addApi(ModelApiReq req, String modelUuid, ModelDo modelDo) {
        ApiDo apiDo = new ApiDo();

        apiDo.setUuid(modelUuid);
        apiDo.setName(req.getModelName());
        apiDo.setIntro(req.getTectIntro());
        apiDo.setDesc(req.getSceneIntro());
        apiDo.setImage(req.getImage());
        apiDo.setUri(req.getApiUrl());
        apiDo.setDemo(req.getDemo());
        apiDo.setPageDemo(req.getPageDemo());
        apiDo.setStatus(30);
        apiDo.setModelUuid(modelUuid);
        apiDo.setModelId(modelDo.getId());
        apiDo.setStyle(req.getStyle());
        apiDo.setBaseModelUid(req.getBaseModelUid());

        apiDao.insert(apiDo);
    }


    private ModelDo addModel(ModelApiReq req, String modelUuid) {
        ModelDo modelDo = new ModelDo();
        modelDo.setUuid(modelUuid);

        modelDo.setName(req.getModelName());
        modelDo.setIntro(req.getTectIntro());
        modelDo.setDesc(req.getSceneIntro());
        modelDo.setImage(req.getImage());
        modelDo.setStyle(req.getStyle());
        modelDo.setSubject(20);
        modelDo.setStatus(90);
        modelDo.setGroupUuid(req.getGroupUuid());
        modelDo.setGroupId(req.getGroupId());
        modelDo.setCreateBy(String.valueOf(req.getUserId()));

        modelDao.insert(modelDo);
        return modelDo;
    }

    private TaskDo addTask(ModelApiReq req, ModelDo modelDo) {
        TaskDo task = new TaskDo();

        String taskUuid = req.getTaskUuid();
        if (!StringUtils.isBlank(taskUuid)){
            task.setUuid(taskUuid);
        }else {
            task.setUuid(IdGenerator.getId());
        }


        task.setName(req.getModelName());
        task.setImage(req.getImage());

        task.setIntro(req.getSceneIntro());
        task.setDesc(req.getTectIntro());
        task.setModelId(modelDo.getId());

        task.setModelUuid(modelDo.getUuid());

        task.setModelUpdateTime(new Date());

        task.setGroupId(req.getGroupId());
        task.setGroupUuid(req.getGroupUuid());

        //10,20,30
        task.setScope(30);
        task.setStatus(90);
        task.setAppShortName(req.getAppShortName());
        task.setSlogan(req.getSlogan());
        taskDao.insert(task);
        return task;
    }

}
