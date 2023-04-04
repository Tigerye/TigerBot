package com.tigerobo.x.pai.dal.pay.dao;

import com.tigerobo.x.pai.dal.pay.entity.PaymentApiDetailPo;
import com.tigerobo.x.pai.dal.pay.mapper.PaymentApiDetailMapper;
import com.tigerobo.x.pai.dal.pay.mapper.PaymentApiDetailQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PaymentApiDetailDao {

    @Autowired
    private PaymentApiDetailMapper apiDetailMapper;

    @Autowired
    private PaymentApiDetailQueryMapper paymentApiDetailQueryMapper;

    public List<PaymentApiDetailPo> getUserPeriodList(Integer userId,int startDay,int endDay){

        Example example = new Example(PaymentApiDetailPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andGreaterThanOrEqualTo("day",startDay);
        criteria.andLessThanOrEqualTo("day",endDay);

        criteria.andEqualTo("consumeType",0);
        criteria.andGreaterThan("agreementId",0);

        criteria.andEqualTo("isDeleted",0);

        return apiDetailMapper.selectByExample(example);

    }



    public PaymentApiDetailPo getByKey(int userId,int agreementId,int day,int consumeType){

        Example example = new Example(PaymentApiDetailPo.class);
        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("day",day);
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("agreementId",agreementId);
        criteria.andEqualTo("consumeType",consumeType);

        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");
        final List<PaymentApiDetailPo> pos = apiDetailMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.get(0);


    }

    public List<PaymentApiDetailPo> getUserDetails(Integer userId,Integer day,Integer consumeType,Integer status){


        Example example = new Example(PaymentApiDetailPo.class);
        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("day",day);
        criteria.andEqualTo("userId",userId);
        if (consumeType!=null){
            criteria.andEqualTo("consumeType",consumeType);
        }
        if (status!=null){
            criteria.andEqualTo("status",status);
        }

        criteria.andEqualTo("isDeleted",0);

        return apiDetailMapper.selectByExample(example);

    }

    public List<PaymentApiDetailPo> getUserDetails(Integer userId,String modelId,Integer day,Integer consumeType,Integer status,List<Integer> cardIds){


        Example example = new Example(PaymentApiDetailPo.class);
        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("day",day);
        criteria.andEqualTo("userId",userId);
        if (!StringUtils.isEmpty(modelId)){
            criteria.andEqualTo("modelId",modelId);
        }
        if (consumeType!=null){
            criteria.andEqualTo("consumeType",consumeType);
        }
        if (status!=null){
            criteria.andEqualTo("status",status);
        }
        if (!CollectionUtils.isEmpty(cardIds)){
            criteria.andIn("agreementId",cardIds);
        }

        criteria.andEqualTo("isDeleted",0);

        return apiDetailMapper.selectByExample(example);

    }

    public int add(PaymentApiDetailPo po){
        return apiDetailMapper.insertSelective(po);
    }

    public int update(PaymentApiDetailPo po){
        return apiDetailMapper.updateByPrimaryKeySelective(po);
    }

    public PaymentApiDetailPo load(Integer id){

        final PaymentApiDetailPo po = apiDetailMapper.selectByPrimaryKey(id);

        if (po == null||po.getIsDeleted()==null||po.getIsDeleted()){
            return null;
        }
        return po;
    }

    public Integer sumUserCallNum(String modelId, int userId, int startDay, int endDay){
        return paymentApiDetailQueryMapper.sumCallNum(modelId,userId,startDay,endDay);
    }

    public List<Integer> getCardConsumeWaitDealUserIdsByDay(int day){

        return paymentApiDetailQueryMapper.getCardConsumeWaitDealUserIdsByDay(day);
    }
}
