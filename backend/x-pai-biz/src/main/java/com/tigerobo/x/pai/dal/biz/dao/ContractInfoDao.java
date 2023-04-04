package com.tigerobo.x.pai.dal.biz.dao;

import com.tigerobo.x.pai.dal.biz.entity.ContractInfoPo;
import com.tigerobo.x.pai.dal.biz.mapper.ContractInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ContractInfoDao {

    @Autowired
    private ContractInfoMapper contractInfoMapper;


    public ContractInfoPo getById(Integer id){

        Example example = new Example(ContractInfoPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",id);
        addDeleted(criteria);
        return contractInfoMapper.selectOneByExample(example);
    }

    public void add(ContractInfoPo contractInfoPo){

        if (contractInfoPo == null){
            return;
        }
        contractInfoMapper.insertSelective(contractInfoPo);
    }

    public void update(ContractInfoPo contractInfoPo){
        if (contractInfoPo == null||contractInfoPo.getId()==null){
            return;
        }
        contractInfoMapper.updateByPrimaryKeySelective(contractInfoPo);
    }

    public ContractInfoPo getContractByDemandId(String demandId){

        Example example = new Example(ContractInfoPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bizId",demandId);
        addDeleted(criteria);

        List<ContractInfoPo> contractInfoPos = contractInfoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(contractInfoPos)){
            return null;
        }
        return contractInfoPos.get(0);
    }

    private void addDeleted(Example.Criteria criteria) {
        criteria.andEqualTo("isDeleted",0);
    }


}
