package com.tigerobo.x.pai.dal.pay.dao;

import com.tigerobo.x.pai.dal.pay.entity.PaymentApiBillPo;
import com.tigerobo.x.pai.dal.pay.mapper.PaymentApiBillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Component
public class PaymentApiBillDao {

    @Autowired
    private PaymentApiBillMapper paymentApiBillMapper;

    public List<PaymentApiBillPo> getUserBills(int userId) {

        Example example = new Example(PaymentApiBillPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);

        criteria.andEqualTo("isDeleted", 0);

        example.setOrderByClause("billPeriod desc");
        return paymentApiBillMapper.selectByExample(example);
    }

    public PaymentApiBillPo getByKey(int userId, int billPeriod) {

        Example example = new Example(PaymentApiBillPo.class);

        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("billPeriod", billPeriod);
        criteria.andEqualTo("userId", userId);

        criteria.andEqualTo("isDeleted", 0);

        example.setOrderByClause("id desc");
        final List<PaymentApiBillPo> pos = paymentApiBillMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(pos)) {
            return null;
        }
        return pos.get(0);
    }

    public int add(PaymentApiBillPo po) {
        return paymentApiBillMapper.insertSelective(po);
    }


    public int update(PaymentApiBillPo po) {
        return paymentApiBillMapper.updateByPrimaryKeySelective(po);
    }

    public PaymentApiBillPo load(Integer id) {

        final PaymentApiBillPo po = paymentApiBillMapper.selectByPrimaryKey(id);

        if (po == null || po.getIsDeleted() == null || po.getIsDeleted()) {
            return null;
        }
        return po;
    }


    public int updatePaySuccess(String orderNo, Date payTime) {

        Example example = new Example(PaymentApiBillPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderNo", orderNo);

        PaymentApiBillPo po = new PaymentApiBillPo();
        po.setStatus(2);
        po.setPayTime(payTime);

        return paymentApiBillMapper.updateByExampleSelective(po, example);
    }
}
