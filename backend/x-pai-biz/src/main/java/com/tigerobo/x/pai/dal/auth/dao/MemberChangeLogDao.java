package com.tigerobo.x.pai.dal.auth.dao;

import com.tigerobo.x.pai.dal.auth.entity.MemberChangeLogPo;
import com.tigerobo.x.pai.dal.auth.mapper.MemberChangeLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberChangeLogDao {

    @Autowired
    private MemberChangeLogMapper memberChangeLogMapper;

    public void addLog(MemberChangeLogPo po){
        memberChangeLogMapper.insertSelective(po);
    }
}
