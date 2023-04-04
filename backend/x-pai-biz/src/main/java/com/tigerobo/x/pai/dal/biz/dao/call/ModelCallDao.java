package com.tigerobo.x.pai.dal.biz.dao.call;

import com.tigerobo.x.pai.api.eye.req.ModelBoardReq;
import com.tigerobo.x.pai.dal.biz.entity.call.ModelCallPo;
import com.tigerobo.x.pai.dal.biz.mapper.call.ModelCallMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ModelCallDao {

    @Autowired
    private ModelCallMapper modelCallMapper;


    public ModelCallPo getByKey(int day,int modelId,int userId){

        Example example = new Example(ModelCallPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("day",day);
        criteria.andEqualTo("modelId",modelId);
        criteria.andEqualTo("userId",userId);
        return modelCallMapper.selectOneByExample(example);
    }

    public void add(ModelCallPo po){
        modelCallMapper.insertSelective(po);
    }

    public void update(ModelCallPo po){


        modelCallMapper.updateByPrimaryKeySelective(po);
    }

    public List<ModelCallPo> getCallByDay(ModelBoardReq req,List<Integer> modelIds){

        Example example = new Example(ModelCallPo.class);
        Example.Criteria criteria = example.createCriteria();

        if (req.getStartDay()!=null){
            criteria.andGreaterThanOrEqualTo("day",req.getStartDay());
        }
        if (req.getEndDay()!=null){
            criteria.andLessThanOrEqualTo("day",req.getEndDay());
        }
        if (req.getUserId()!=null){
            criteria.andEqualTo("userId",req.getUserId());
        }

        if (!CollectionUtils.isEmpty(modelIds)){
            criteria.andIn("modelId",modelIds);
        }

        return modelCallMapper.selectByExample(example);
    }
}
