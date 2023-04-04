package com.tigerobo.x.pai.dal.aml.dao;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tigerobo.x.pai.api.aml.enums.ModelServiceStatusEnum;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.dal.aml.mapper.AmlModelMapper;
import com.tigerobo.x.pai.dal.biz.entity.ModelDo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Component
public class AmlModelDao {

    @Autowired
    private AmlModelMapper amlModelMapper;

    public List<AmlModelDo> getByModelIds(List<Integer> modelIds){
        if (CollectionUtils.isEmpty(modelIds)){
            return null;
        }

        Example example = new Example(AmlModelDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",modelIds);
        criteria.andEqualTo("isDeleted",0);
        return amlModelMapper.selectByExample(example);
    }

    public int countMine(String createBy){
        if (StringUtils.isEmpty(createBy)){
            return 0;
        }

        Example example = new Example(AmlModelDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createBy",createBy);
        criteria.andEqualTo("status",AmlStatusEnum.TRAIN_SUCCESS.getStatus());
        criteria.andEqualTo("isDeleted",0);
        return amlModelMapper.selectCountByExample(example);
    }

    public PageInfo<AmlModelDo> getMyModelList(String userId, Integer pageNo, Integer pageSize, String orderBy ){

        Example example = new Example(AmlModelDo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("createBy",userId);
        criteria.andEqualTo("status",AmlStatusEnum.TRAIN_SUCCESS.getStatus());
        criteria.andEqualTo("isDeleted",0);
        PageHelper.startPage(pageNo,pageSize);
        example.setOrderByClause("id desc");
        List<AmlModelDo> amlInfoDos = amlModelMapper.selectByExample(example);
        return new PageInfo<>(amlInfoDos);

    }

    public int insert(AmlModelDo modelDo){
        return amlModelMapper.insertSelective(modelDo);
    }




    public AmlModelDo loadIgnore(Integer id){
        return amlModelMapper.selectByPrimaryKey(id);
    }

    public AmlModelDo getById(Integer id){
        AmlModelDo modelDo = loadById(id);
        if (modelDo!=null&&modelDo.getIsDeleted()){
            return null;
        }
        return modelDo;
    }


    public AmlModelDo loadById(Integer id){
        return amlModelMapper.selectByPrimaryKey(id);
    }


    public List<AmlModelDo> getWaitTrainList(){

        Example example = new Example(AmlModelDo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("status", (int)AmlStatusEnum.WAIT_TRAIN.getStatus());

        criteria.andEqualTo("isDeleted",0);

//        PageHelper.startPage(1,10);

        return amlModelMapper.selectByExample(example);

    }

    public void update(AmlModelDo modelDo){

        amlModelMapper.updateByPrimaryKeySelective(modelDo);
    }


    public int countOnTrainProcessList(){

        Example example = new Example(AmlModelDo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("status", AmlStatusEnum.ON_PROCESS_TRAIN.getStatus());

        criteria.andEqualTo("isDeleted",0);

        return amlModelMapper.selectCountByExample(example);
    }
    public List<AmlModelDo> getOnTrainProcessList(){

        Example example = new Example(AmlModelDo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("status", AmlStatusEnum.ON_PROCESS_TRAIN.getStatus());

        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1,10);

        return amlModelMapper.selectByExample(example);
    }

    public List<AmlModelDo> getWaitServiceOnlineList(){
        Example example = new Example(AmlModelDo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("serviceStatus", ModelServiceStatusEnum.WAIT_ONLINE.getStatus());
        criteria.andEqualTo("isDeleted",0);
//
//        Date date = DateUtils.addMinutes(new Date(), -15);
//        criteria.andEqualTo("modelFinishTime",date);
        PageHelper.startPage(1,30);
        return amlModelMapper.selectByExample(example);
    }


    public List<AmlModelDo> getWaitServiceOfflineList(){
        Example example = new Example(AmlModelDo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("serviceStatus", ModelServiceStatusEnum.WAIT_OFFLINE.getStatus());
        criteria.andEqualTo("isDeleted",0);
//
//        Date date = DateUtils.addMinutes(new Date(), -15);
//        criteria.andEqualTo("modelFinishTime",date);
        PageHelper.startPage(1,60);
        return amlModelMapper.selectByExample(example);
    }
}
