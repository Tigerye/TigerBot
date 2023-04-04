package com.tigerobo.x.pai.dal.aml.dao;

import com.tigerobo.x.pai.dal.aml.entity.AmlDatasetDo;
import com.tigerobo.x.pai.dal.aml.mapper.AmlDatasetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class AmlDatasetDao {

    @Autowired
    private AmlDatasetMapper amlDatasetMapper;

    public int insert(AmlDatasetDo datasetDo){
        return amlDatasetMapper.insertSelective(datasetDo);
    }

    public AmlDatasetDo getById(Integer id){
        return amlDatasetMapper.selectByPrimaryKey(id);
    }


    public List<AmlDatasetDo> getByIds(List<Integer> ids){

        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        Example example = new Example(AmlDatasetDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        return amlDatasetMapper.selectByExample(example);
    }
    public void update(AmlDatasetDo datasetDo){
        amlDatasetMapper.updateByPrimaryKeySelective(datasetDo);
    }
}
