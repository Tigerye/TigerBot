package com.tigerobo.x.pai.dal.biz.dao;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.dal.biz.entity.ModelDo;
import com.tigerobo.x.pai.dal.biz.mapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ModelDao {

    @Autowired
    private ModelMapper modelMapper;


    public List<ModelDo> getBaseModels(){
        Example example = new Example(ModelDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("subject", 90);//Model.Subject.BASE_MODEL.getVal());
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("id desc");
        return modelMapper.selectByExample(example);
    }

    public List<ModelDo> query(List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        Example example = new Example(ModelDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");

        return modelMapper.selectByExample(example);
    }

    public void update(ModelDo modelDo){
        modelMapper.updateByPrimaryKeySelective(modelDo);
    }
    public ModelDo getByUuid(String uuid){
        if (StringUtils.isEmpty(uuid)){
            return null;
        }
        Example example = new Example(ModelDo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("uuid",uuid);
        List<ModelDo> modelDos = modelMapper.selectByExample(example);
        return CollectionUtils.isEmpty(modelDos)?null:modelDos.get(0);
    }

    public List<ModelDo> getByUuids(List<String> uuidList){
        if (CollectionUtils.isEmpty(uuidList)){
            return null;
        }
        Example example = new Example(ModelDo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("uuid",uuidList);
        return modelMapper.selectByExample(example);
    }

    public void insert(ModelDo modelDo){
        if (modelDo == null){
            return;
        }

        modelMapper.insertSelective(modelDo);
    }


    public List<ModelDo> getUserModelList(String createBy){

        Example example = new Example(ModelDo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("createBy", createBy);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");

        PageHelper.startPage(1,100);

        return modelMapper.selectByExample(example);
    }
}
