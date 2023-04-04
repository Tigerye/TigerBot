package com.tigerobo.x.pai.dal.ai.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.dal.ai.entity.AiDailyLimitPo;
import com.tigerobo.x.pai.dal.ai.mapper.AiDailyLimitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Component
public class AiDailyLimitDao {

    @Autowired
    private AiDailyLimitMapper aiDailyLimitMapper;

    public Page<AiDailyLimitPo> getPage(Integer pageNo,Integer pageSize){

        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(AiDailyLimitPo.class);
        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("isDeleted",0);

        final List<AiDailyLimitPo> aiDailyLimitPos = aiDailyLimitMapper.selectByExample(example);
        return (Page<AiDailyLimitPo>)aiDailyLimitPos;
    }


    public AiDailyLimitPo loadByUserId(Integer userId){

        Example example = new Example(AiDailyLimitPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("isDeleted",0);

        final List<AiDailyLimitPo> pos = aiDailyLimitMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.get(0);
    }

    public AiDailyLimitPo load(Integer id){

        Example example = new Example(AiDailyLimitPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",id);
        criteria.andEqualTo("isDeleted",0);

        final List<AiDailyLimitPo> pos = aiDailyLimitMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.get(0);
    }

    public void add(AiDailyLimitPo po){
        aiDailyLimitMapper.insertSelective(po);
    }

    public int update(AiDailyLimitPo po){
        return aiDailyLimitMapper.updateByPrimaryKeySelective(po);
    }
}
