package com.tigerobo.x.pai.dal.biz.dao.user;

import com.tigerobo.x.pai.dal.biz.entity.user.UserShareLogPo;
import com.tigerobo.x.pai.dal.biz.mapper.user.UserShareLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserShareLogDao {

    @Autowired
    private UserShareLogMapper shareLogMapper;
    public void add(UserShareLogPo po){
        shareLogMapper.insertSelective(po);
    }
}
