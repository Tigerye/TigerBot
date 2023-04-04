package com.tigerobo.x.pai.dal.biz.dao.offline;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.dal.biz.entity.offline.ModelBatchTaskPo;
import com.tigerobo.x.pai.dal.biz.mapper.offline.ModelBatchTaskMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ModelBatchTaskDao {

    @Autowired
    private ModelBatchTaskMapper modelBatchTaskMapper;

    public ModelBatchTaskPo load(int id ){
        return modelBatchTaskMapper.selectByPrimaryKey(id);
    }


    @Transactional(transactionManager = "paiTm")
    public ModelBatchTaskPo loadWithNoCache(int id ){
        return modelBatchTaskMapper.selectByPrimaryKey(id);
    }
    public ModelBatchTaskPo getByReqId(Long reqId){

        Example example =  new Example(ModelBatchTaskPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("reqId",reqId);

        List<ModelBatchTaskPo> taskPos = modelBatchTaskMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(taskPos)){
            return null;
        }
        return taskPos.get(0);
    }

    public List<ModelBatchTaskPo> getUserList(Integer userId,String bizId,Integer type,int pageNo,int pageSize){

        Example example =  new Example(ModelBatchTaskPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);

        if (StringUtils.isNotBlank(bizId)){
            criteria.andEqualTo("bizId",bizId);
        }
        if (type!=null){
            criteria.andEqualTo("bizType",type);
        }
        example.setOrderByClause("id desc");
        PageHelper.startPage(pageNo,pageSize);
        return modelBatchTaskMapper.selectByExample(example);
    }
    public void add(ModelBatchTaskPo po){

        modelBatchTaskMapper.insertSelective(po);
    }
    public int update(ModelBatchTaskPo po){
        return modelBatchTaskMapper.updateByPrimaryKeySelective(po);
    }


    public List<ModelBatchTaskPo> getWaitDealList(){

        Example example = new Example(ModelBatchTaskPo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("status",0);

        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1,20);

        return modelBatchTaskMapper.selectByExample(example);
    }


    public List<ModelBatchTaskPo> getWaitDealListTest(){

        Example example = new Example(ModelBatchTaskPo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("id",330);
//        criteria.andEqualTo("status",0);

        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1,20);

        return modelBatchTaskMapper.selectByExample(example);
    }
}
