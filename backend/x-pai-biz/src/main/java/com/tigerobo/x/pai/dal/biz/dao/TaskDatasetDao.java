package com.tigerobo.x.pai.dal.biz.dao;

import com.tigerobo.x.pai.dal.biz.entity.TaskDatasetDo;
import com.tigerobo.x.pai.dal.biz.mapper.TaskDatasetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class TaskDatasetDao {

    @Autowired
    private TaskDatasetMapper taskDatasetMapper;

    public void insert(TaskDatasetDo taskDatasetDo){

        if (taskDatasetDo == null){
            return;
        }
        taskDatasetMapper.insertSelective(taskDatasetDo);
    }


    public void del(String taskId,String datasetId,Integer userId){
        Example example = new Example(TaskDatasetDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("taskUuid",taskId);
        criteria.andEqualTo("datasetUuid",datasetId);
        criteria.andEqualTo("isDeleted",0);
        TaskDatasetDo datasetDo = new TaskDatasetDo();
        datasetDo.setIsDeleted(true);
        if (userId!=null){
            datasetDo.setUpdateBy(String.valueOf(userId));
        }
        taskDatasetMapper.updateByExampleSelective(datasetDo,example);
    }

    public List<TaskDatasetDo> getByTaskUuid(String uuid){

        Example example = new Example(TaskDatasetDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("taskUuid",uuid);
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("id desc");
        return taskDatasetMapper.selectByExample(example);
    }
}
