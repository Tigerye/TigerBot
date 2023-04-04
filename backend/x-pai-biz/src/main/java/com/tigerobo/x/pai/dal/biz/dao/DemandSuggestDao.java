package com.tigerobo.x.pai.dal.biz.dao;

import com.tigerobo.x.pai.dal.biz.entity.DemandSuggestDo;
import com.tigerobo.x.pai.dal.biz.mapper.DemandSuggestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class DemandSuggestDao {

    @Autowired
    private DemandSuggestMapper demandSuggestMapper;



    public void add(DemandSuggestDo demandSuggestDo){
        demandSuggestMapper.insertSelective(demandSuggestDo);
    }

    public List<DemandSuggestDo> getDemandList(String uuid){

        Example example = new Example(DemandSuggestDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("demandUuid",uuid);

        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");

        return demandSuggestMapper.selectByExample(example);
    }
}
