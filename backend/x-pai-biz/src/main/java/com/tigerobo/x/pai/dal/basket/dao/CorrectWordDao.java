package com.tigerobo.x.pai.dal.basket.dao;

import com.tigerobo.x.pai.dal.basket.entity.CorrectWordPo;
import com.tigerobo.x.pai.dal.basket.mapper.CorrectWordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CorrectWordDao {

    @Autowired
    private CorrectWordMapper correctWordMapper;

    public void addList(List<CorrectWordPo> pos){

    }

    public void add(CorrectWordPo po){
        if ( po!=null){
            correctWordMapper.insertSelective(po);
        }
    }
}
