package com.tigerobo.x.pai.dal.basket.dao;

import com.tigerobo.x.pai.dal.basket.entity.BasketModelCallPo;
import com.tigerobo.x.pai.dal.basket.mapper.BasketModelCallMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasketModelCallDao {


    @Autowired
    private BasketModelCallMapper basketModelCallMapper;

    public void add(BasketModelCallPo po){
        basketModelCallMapper.insertSelective(po);
    }
}
