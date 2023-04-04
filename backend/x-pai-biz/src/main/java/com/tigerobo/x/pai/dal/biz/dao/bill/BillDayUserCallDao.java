package com.tigerobo.x.pai.dal.biz.dao.bill;


import com.tigerobo.x.pai.dal.biz.entity.bill.BillDayUserCallPo;
import com.tigerobo.x.pai.dal.biz.mapper.bill.BillDayUserCallMapper;
import com.tigerobo.x.pai.dal.biz.mapper.bill.BillQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class BillDayUserCallDao {

    @Autowired
    private BillDayUserCallMapper billDayUserCallMapper;

    @Autowired
    private BillQueryMapper billQueryMapper;


    public List<BillDayUserCallPo> getList(int startDay,int endDay,Integer userId){

        Example example = new Example(BillDayUserCallPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThanOrEqualTo("day",startDay);
        criteria.andLessThanOrEqualTo("day",endDay);
        criteria.andEqualTo("userId",userId);
        return billDayUserCallMapper.selectByExample(example);
    }



    public BillDayUserCallPo getByKey(int day, int userId){

        Example example = new Example(BillDayUserCallPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("day",day);
        criteria.andEqualTo("userId",userId);
        return billDayUserCallMapper.selectOneByExample(example);
    }

    public void add(BillDayUserCallPo po){
        billDayUserCallMapper.insertSelective(po);
    }

    public void update(BillDayUserCallPo po){


        billDayUserCallMapper.updateByPrimaryKeySelective(po);
    }

}
