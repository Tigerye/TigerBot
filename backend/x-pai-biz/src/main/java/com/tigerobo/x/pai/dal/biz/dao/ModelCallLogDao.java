package com.tigerobo.x.pai.dal.biz.dao;

import com.tigerobo.x.pai.dal.biz.entity.ModelCallLogPo;
import com.tigerobo.x.pai.dal.biz.mapper.ModelCallLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModelCallLogDao {

    @Autowired
    private ModelCallLogMapper modelCallLogMapper;
    public void add(ModelCallLogPo logPo){
        modelCallLogMapper.insertSelective(logPo);
    }

}
