package com.tigerobo.x.pai.dal.aml.dao;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tigerobo.x.pai.dal.aml.entity.AmlInfoDo;
import com.tigerobo.x.pai.dal.aml.mapper.AmlInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class AmlInfoDao {
    @Autowired
    private AmlInfoMapper amlInfoMapper;


    public AmlInfoDo getById(Integer amlId){
        AmlInfoDo amlInfoDo = loadById(amlId);
        if (amlInfoDo!=null&&amlInfoDo.getIsDeleted()){
            return null;
        }
        return amlInfoDo;
    }

    public AmlInfoDo loadById(Integer amlId){
        return amlInfoMapper.selectByPrimaryKey(amlId);
    }


    public Integer insert(AmlInfoDo infoDo){
        return amlInfoMapper.insertSelective(infoDo);
    }

    public int updateByPrimaryKey(AmlInfoDo updateInfo) {
        return amlInfoMapper.updateByPrimaryKeySelective(updateInfo);
    }


    public PageInfo<AmlInfoDo> getMyAmlList(String userId,Integer pageNo,Integer pageSize,String orderBy ){

        Example example = new Example(AmlInfoDo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("createBy",userId);

        criteria.andEqualTo("isDeleted",0);
        criteria.andEqualTo("hide",0);
        PageHelper.startPage(pageNo,pageSize);

        if (StringUtils.isNotEmpty(orderBy)){
            example.setOrderByClause(orderBy);
        }else {
            example.setOrderByClause("id desc");
        }
        List<AmlInfoDo> amlInfoDos = amlInfoMapper.selectByExample(example);
        return new PageInfo<>(amlInfoDos);

    }
}
