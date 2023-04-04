package com.tigerobo.x.pai.dal.biz.dao.eye;

import com.tigerobo.x.pai.dal.biz.entity.eye.EyeDailyCountPo;
import com.tigerobo.x.pai.dal.biz.mapper.eye.EyeDailyCountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class EyeDailyCountDao {

    @Autowired
    private EyeDailyCountMapper eyeDailyCountMapper;

    public void add(EyeDailyCountPo po){
        eyeDailyCountMapper.insertSelective(po);
    }

    public void update(EyeDailyCountPo po){
        eyeDailyCountMapper.updateByPrimaryKeySelective(po);
    }


    public List<EyeDailyCountPo> getDayList(Integer day,Integer type){

        Example example = new Example(EyeDailyCountPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("day",day);
        if (type!=null){
            criteria.andEqualTo("type",type);
        }
        return eyeDailyCountMapper.selectByExample(example);
    }
    public List<EyeDailyCountPo> getList(Integer startDay,Integer endDay,Integer type){

        Example example = new Example(EyeDailyCountPo.class);

        Example.Criteria criteria = example.createCriteria();

        if (startDay!=null){
            criteria.andGreaterThanOrEqualTo("day",startDay);
        }

        if (endDay!=null){
            criteria.andLessThanOrEqualTo("day",endDay);
        }
        criteria.andEqualTo("type",type);

        return eyeDailyCountMapper.selectByExample(example);
    }

}
