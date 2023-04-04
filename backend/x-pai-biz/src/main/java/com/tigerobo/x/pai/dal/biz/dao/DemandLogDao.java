package com.tigerobo.x.pai.dal.biz.dao;

import com.tigerobo.x.pai.dal.biz.entity.DemandLogPo;
import com.tigerobo.x.pai.dal.biz.mapper.DemandLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemandLogDao {

    @Autowired
    private DemandLogMapper demandLogMapper;
    public void addLog(DemandLogPo demandLogPo){

        if (demandLogPo == null){
            return;
        }
        demandLogMapper.insertSelective(demandLogPo);
    }
}
