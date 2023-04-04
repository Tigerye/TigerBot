package com.tigerobo.x.pai.dal.biz.dao;

import com.tigerobo.x.pai.dal.biz.entity.DatasetDo;
import com.tigerobo.x.pai.dal.biz.mapper.DatasetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatasetDao {
    @Autowired
    private DatasetMapper datasetMapper;

    public void insert(DatasetDo po){

        if (po ==null){
            return;
        }

        datasetMapper.insertSelective(po);
    }

    public List<DatasetDo> getByIds(List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)){
            return new ArrayList<>();
        }
        Example example = new Example(DatasetDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("id desc");
        return datasetMapper.selectByExample(example);
    }


}
