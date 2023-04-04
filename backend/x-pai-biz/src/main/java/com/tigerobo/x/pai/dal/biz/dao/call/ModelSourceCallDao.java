package com.tigerobo.x.pai.dal.biz.dao.call;

import com.tigerobo.x.pai.dal.biz.entity.call.ModelSourceCallPo;
import com.tigerobo.x.pai.dal.biz.mapper.call.ModelSourceCallMapper;
import com.tigerobo.x.pai.dal.biz.mapper.call.SourceCallQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ModelSourceCallDao {

    @Autowired
    private ModelSourceCallMapper modelSourceCallMapper;


    @Autowired
    private SourceCallQueryMapper sourceCallQueryMapper;


    public List<ModelSourceCallPo> getUserDayListAmlOnlineCall(int userId,int day,Integer numLimit){

        Example example = new Example(ModelSourceCallPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("day",day);
        criteria.andEqualTo("userId",userId);

        criteria.andEqualTo("source",2);
        criteria.andEqualTo("type",2);
        if (numLimit!=null){
            criteria.andGreaterThan("num",numLimit);
        }

        example.setOrderByClause("num desc");

        return modelSourceCallMapper.selectByExample(example);
    }


    public List<Integer> getDayUserIdsForConsume(int day){
        return sourceCallQueryMapper.getDayUserIds(day);
    }


    public List<ModelSourceCallPo> getUserDetailList(int startDay,int endDay,Integer userId,Integer source){

        Example example = new Example(ModelSourceCallPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andGreaterThanOrEqualTo("day",startDay);
        criteria.andLessThanOrEqualTo("day",endDay);
        criteria.andEqualTo("userId",userId);
        if (source!=null){
            criteria.andEqualTo("source",source);
        }


        return modelSourceCallMapper.selectByExample(example);
    }


    public List<ModelSourceCallPo> getByDay(int day,Integer source,Integer type){

        Example example = new Example(ModelSourceCallPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("day",day);
        if (source!=null){
            criteria.andEqualTo("source",source);
        }
        if (type !=null){
            criteria.andEqualTo("type",type);
        }
        return modelSourceCallMapper.selectByExample(example);
    }

    public ModelSourceCallPo getByKey(int day,int modelId,int userId,int source,int type){

        Example example = new Example(ModelSourceCallPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("day",day);
        criteria.andEqualTo("modelId",modelId);
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("source",source);
        criteria.andEqualTo("type",type);
        return modelSourceCallMapper.selectOneByExample(example);
    }



    public List<ModelSourceCallPo> getUserDayCallList(int day,int userId,String modelId,List<Integer> sourceList,Integer type){

        Example example = new Example(ModelSourceCallPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("day",day);
        criteria.andEqualTo("userId",userId);
        if (!StringUtils.isEmpty(modelId)){
            criteria.andEqualTo("modelId",modelId);
        }
        if (!CollectionUtils.isEmpty(sourceList)){
            criteria.andIn("source",sourceList);
        }
        if (type!=null){
            criteria.andEqualTo("type",type);
        }
        return modelSourceCallMapper.selectByExample(example);

    }

    public void add(ModelSourceCallPo po){
        modelSourceCallMapper.insertSelective(po);
    }

    public void update(ModelSourceCallPo po){


        modelSourceCallMapper.updateByPrimaryKeySelective(po);
    }

}
