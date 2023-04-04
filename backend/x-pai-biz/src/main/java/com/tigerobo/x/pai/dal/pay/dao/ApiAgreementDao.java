package com.tigerobo.x.pai.dal.pay.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.dal.pay.entity.ApiAgreementPo;
import com.tigerobo.x.pai.dal.pay.mapper.ApiAgreementMapper;
import com.tigerobo.x.pai.dal.pay.mapper.ApiAgreementQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ApiAgreementDao {

    @Autowired
    private ApiAgreementMapper apiAgreementMapper;


    @Autowired
    private ApiAgreementQueryMapper apiAgreementQueryMapper;



    public List<Integer> getUserIds(){

//        PageHelper.startPage(pageNo,pageSize);

        return apiAgreementQueryMapper.getUserIds();
    }

    public List<ApiAgreementPo> load(List<Integer> ids){

        Example example = new Example(ApiAgreementPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        criteria.andEqualTo("isDeleted",0);
        return apiAgreementMapper.selectByExample(example);
    }

    public int add(ApiAgreementPo po) {
        return apiAgreementMapper.insertSelective(po);
    }

    public int update(ApiAgreementPo po) {
        return apiAgreementMapper.updateByPrimaryKeySelective(po);
    }

    public ApiAgreementPo load(Integer id) {

        final ApiAgreementPo po = apiAgreementMapper.selectByPrimaryKey(id);

        if (po == null || po.getIsDeleted() == null || po.getIsDeleted()) {
            return null;
        }
        return po;
    }

    public ApiAgreementPo getUserModelAgreement(Integer userId, String modelId) {

        Example example = new Example(ApiAgreementPo.class);
        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("modelId",modelId);
        criteria.andEqualTo("isDeleted", 0);

        final List<ApiAgreementPo> apiAgreementPos = apiAgreementMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(apiAgreementPos)){
            return null;
        }

        return apiAgreementPos.get(0);
    }

    public List<ApiAgreementPo> getUserAgreements(Integer userId) {

        Example example = new Example(ApiAgreementPo.class);
        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("isDeleted", 0);

        return apiAgreementMapper.selectByExample(example);


    }

}
