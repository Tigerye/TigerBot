package com.tigerobo.x.pai.dal.pay.dao;

import com.tigerobo.x.pai.dal.pay.entity.ApiBalanceCardPo;
import com.tigerobo.x.pai.dal.pay.mapper.ApiBalanceCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ApiBalanceCardDao {

    @Autowired
    private ApiBalanceCardMapper apiBalanceCardMapper;
    

    public List<ApiBalanceCardPo> load(List<Integer> ids){

        Example example = new Example(ApiBalanceCardPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        criteria.andEqualTo("isDeleted",0);
        return apiBalanceCardMapper.selectByExample(example);
    }

    public int add(ApiBalanceCardPo po) {
        return apiBalanceCardMapper.insertSelective(po);
    }

    public int update(ApiBalanceCardPo po) {
        return apiBalanceCardMapper.updateByPrimaryKeySelective(po);
    }

    public ApiBalanceCardPo load(Integer id) {

        final ApiBalanceCardPo po = apiBalanceCardMapper.selectByPrimaryKey(id);

        if (po == null || po.getIsDeleted() == null || po.getIsDeleted()) {
            return null;
        }
        return po;
    }

    public List<ApiBalanceCardPo> getUserModelFreeCards(Integer userId, String modelId) {

        Example example = new Example(ApiBalanceCardPo.class);
        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("modelId",modelId);
        criteria.andEqualTo("cardType",1);
        criteria.andEqualTo("isDeleted", 0);
        return apiBalanceCardMapper.selectByExample(example);
    }

    public List<ApiBalanceCardPo> getUserCards(Integer userId,String modelId) {

        Example example = new Example(ApiBalanceCardPo.class);
        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        if (!StringUtils.isEmpty(modelId)){
            criteria.andEqualTo("modelId",modelId);
        }
        criteria.andEqualTo("isDeleted", 0);

        example.setOrderByClause("card_type asc");
        return apiBalanceCardMapper.selectByExample(example);

    }

}
