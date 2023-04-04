package com.tigerobo.x.pai.dal.biz.dao;

import com.tigerobo.x.pai.dal.biz.entity.ModelCommitPo;
import com.tigerobo.x.pai.dal.biz.mapper.ModelCommitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ModelCommitDao {

    @Autowired
    private ModelCommitMapper modelCommitMapper;

    public List<ModelCommitPo> getModelCommitList(String modelId){

        Example example = new Example(ModelCommitPo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("modelId",modelId);
        criteria.andEqualTo("isDeleted",0);

        return modelCommitMapper.selectByExample(example);
    }

    public void addCommit(ModelCommitPo commitPo){
        if (commitPo == null){
            return;
        }
        modelCommitMapper.insertSelective(commitPo);
    }


    public ModelCommitPo load(Integer id){
        ModelCommitPo modelCommitPo = modelCommitMapper.selectByPrimaryKey(id);
        if (modelCommitPo==null||modelCommitPo.getIsDeleted()){
            return null;
        }
        return modelCommitPo;
    }

    public void delete(Integer id){
        ModelCommitPo po = new ModelCommitPo();
        po.setId(id);
        po.setIsDeleted(true);
        update(po);
    }


    public void update(ModelCommitPo commitPo){
        if (commitPo == null){
            return;
        }
        modelCommitMapper.updateByPrimaryKeySelective(commitPo);
    }
}
