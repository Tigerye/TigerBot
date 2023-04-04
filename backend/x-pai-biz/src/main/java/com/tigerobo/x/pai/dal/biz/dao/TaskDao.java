package com.tigerobo.x.pai.dal.biz.dao;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.biz.entity.TaskDo;
import com.tigerobo.x.pai.dal.biz.mapper.TaskMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class TaskDao {
    @Autowired
    private TaskMapper taskMapper;

    public TaskDo getByDemandUuid(String uuid){
        if (StringUtils.isBlank(uuid)){
            return null;
        }
        Example example = new Example(TaskDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uuid",uuid);
        criteria.andEqualTo("isDeleted",0);

        List<TaskDo> taskDos = taskMapper.selectByExample(example);
        return CollectionUtils.isEmpty(taskDos)?null:taskDos.get(0);
    }

    public TaskDo getByAppName(String appName){
        if (StringUtils.isBlank(appName)){
            return null;
        }
        Example example = new Example(TaskDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("appShortName",appName);
        criteria.andEqualTo("status",90);

        criteria.andEqualTo("isDeleted",0);

        List<TaskDo> taskDos = taskMapper.selectByExample(example);
        return CollectionUtils.isEmpty(taskDos)?null:taskDos.get(0);

    }
    public TaskDo getByUuid(String uuid){
        if (StringUtils.isBlank(uuid)){
            return null;
        }
        Example example = new Example(TaskDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uuid",uuid);
        criteria.andEqualTo("isDeleted",0);

        List<TaskDo> taskDos = taskMapper.selectByExample(example);
        return CollectionUtils.isEmpty(taskDos)?null:taskDos.get(0);
    }

    public TaskDo getByModelUuid(String modelUuid){
        if (StringUtils.isBlank(modelUuid)){
            return null;
        }
        Example example = new Example(TaskDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("modelUuid",modelUuid);
//        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("is_deleted asc,status desc");
        List<TaskDo> taskDos = taskMapper.selectByExample(example);
        return CollectionUtils.isEmpty(taskDos)?null:taskDos.get(0);
    }

    public List<TaskDo> getModelRelTasks(List<String> modelUuids){
        if (CollectionUtils.isEmpty(modelUuids)){
            return null;
        }
        Example example = new Example(TaskDo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("modelUuid",modelUuids);
        criteria.andEqualTo("status",90);

        criteria.andEqualTo("isDeleted",0);

        return taskMapper.selectByExample(example);
    }


    public List<TaskDo> getListByDemandIds(List<Integer> demandIds){
        if (CollectionUtils.isEmpty(demandIds)){
            return null;
        }
        Example example = new Example(TaskDo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("demandId",demandIds);
        criteria.andEqualTo("isDeleted",0);

        return taskMapper.selectByExample(example);
    }

    public List<TaskDo> getListByUuids(List<String> uuids){
        if (CollectionUtils.isEmpty(uuids)){
            return null;
        }
        Example example = new Example(TaskDo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("uuid",uuids);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");
        return taskMapper.selectByExample(example);
    }


    public void insert(TaskDo taskDo){
        if (taskDo == null){
            return;
        }
        taskMapper.insertSelective(taskDo);
    }

    public void update(TaskDo update) {
        taskMapper.updateByPrimaryKeySelective(update);
    }


    public List<TaskDo> completeList() {
        Example example = new Example(TaskDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status",90);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");
        return taskMapper.selectByExample(example);
    }

    public int count() {
        Example example = new Example(TaskDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status",90);
        criteria.andEqualTo("isDeleted",0);


        return taskMapper.selectCountByExample(example);
    }
}
