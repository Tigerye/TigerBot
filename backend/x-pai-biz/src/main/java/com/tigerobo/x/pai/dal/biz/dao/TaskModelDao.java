package com.tigerobo.x.pai.dal.biz.dao;

import com.tigerobo.x.pai.dal.biz.entity.TaskModelDo;
import com.tigerobo.x.pai.dal.biz.mapper.TaskModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class TaskModelDao {

    @Autowired
    private TaskModelMapper taskModelMapper;

    public TaskModelDo getByModelId(String modelId){

        if (StringUtils.isEmpty(modelId)){
            return null;
        }
        Example example = new Example(TaskModelDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("modelUuid",modelId);
        criteria.andEqualTo("isDeleted",0);
        List<TaskModelDo> taskModelDos = taskModelMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(taskModelDos)){
            return null;
        }
        return taskModelDos.get(0);
    }

    public List<TaskModelDo> getByModelIds(List<String> modelIds){

        if (CollectionUtils.isEmpty(modelIds)){
            return null;
        }
        Example example = new Example(TaskModelDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("modelUuid",modelIds);
        criteria.andEqualTo("isDeleted",0);
        return taskModelMapper.selectByExample(example);
    }

    public List<TaskModelDo> getTaskModels(String uuid){

        if (StringUtils.isEmpty(uuid)){
            return null;
        }
        Example example = new Example(TaskModelDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("taskUuid",uuid);
        criteria.andEqualTo("isDeleted",0);
        return taskModelMapper.selectByExample(example);
    }

    public void insert(TaskModelDo taskModelDo){

        if (taskModelDo==null){
            return;
        }
        taskModelMapper.insertSelective(taskModelDo);
    }

    public int countTaskModels(String uuid){
        Example example = new Example(TaskModelDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("taskUuid",uuid);
        criteria.andEqualTo("isDeleted",0);

        return taskModelMapper.selectCountByExample(example);
    }

    public TaskModelDo getUserTaskModel(String uuid,String createBy){
        if (StringUtils.isEmpty(uuid)||StringUtils.isEmpty(createBy)){
            return null;
        }
        Example example = new Example(TaskModelDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("taskUuid",uuid);
        criteria.andEqualTo("createBy",createBy);
        criteria.andEqualTo("isDeleted",0);

        List<TaskModelDo> taskModelDos = taskModelMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(taskModelDos)){
            return null;
        }
        return taskModelDos.get(0);
    }


    public List<TaskModelDo> getMine(String createBy){
        Example example = new Example(TaskModelDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createBy",createBy);
        criteria.andEqualTo("isDeleted",0);


        return taskModelMapper.selectByExample(example);
    }

    public int countMine(String createBy){
        if (StringUtils.isEmpty(createBy)){
            return 0;
        }
        Example example = new Example(TaskModelDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createBy",createBy);
        criteria.andEqualTo("isDeleted",0);
        return taskModelMapper.selectCountByExample(example);
    }
}
