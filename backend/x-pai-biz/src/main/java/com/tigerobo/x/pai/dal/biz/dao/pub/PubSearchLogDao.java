package com.tigerobo.x.pai.dal.biz.dao.pub;

import com.tigerobo.x.pai.dal.biz.entity.pub.PubSearchLogPo;
import com.tigerobo.x.pai.dal.biz.mapper.pub.PubSearchLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PubSearchLogDao {

    @Autowired
    private PubSearchLogMapper searchLogMapper;

    public void add(PubSearchLogPo po){
        searchLogMapper.insertSelective(po);
    }
}
