package com.tigerobo.x.pai.dal.biz.dao;

import com.tigerobo.x.pai.dal.biz.entity.DemandDatasetPo;
import com.tigerobo.x.pai.dal.biz.mapper.DemandDatasetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class DemandDatasetDao {

    @Autowired
    private DemandDatasetMapper demandDatasetMapper;
    public void insert(DemandDatasetPo po){
        demandDatasetMapper.insertSelective(po);
    }

    public List<DemandDatasetPo> getDemandDatasetList(String demandUuid) {

        Example example = new Example(DemandDatasetPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("demandUuid",demandUuid);
        criteria.andEqualTo("isDeleted",0);

        return demandDatasetMapper.selectByExample(example);
    }

    public void update(DemandDatasetPo po){
        demandDatasetMapper.updateByPrimaryKeySelective(po);
    }
}
