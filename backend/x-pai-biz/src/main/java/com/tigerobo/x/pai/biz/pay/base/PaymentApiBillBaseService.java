package com.tigerobo.x.pai.biz.pay.base;

import com.tigerobo.x.pai.dal.pay.dao.PaymentApiBillDao;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiBillPo;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiDetailPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Service
public class PaymentApiBillBaseService {


    @Autowired
    private PaymentApiBillDao paymentApiBillDao;
    public void addMonthBill(PaymentApiBillPo po){
        final Integer userId = po.getUserId();
        final Integer billPeriod = po.getBillPeriod();
        final PaymentApiBillPo dbPo = paymentApiBillDao.getByKey(userId, billPeriod);
        if (dbPo!=null){
            po.setId(dbPo.getId());
            final boolean same = Objects.equals(po.getEndDay(), dbPo.getEndDay()) && Objects.equals(dbPo.getTotalFee(), po.getTotalFee());
            if (!same){
                paymentApiBillDao.update(po);
            }
        }else {
            paymentApiBillDao.add(po);
        }
    }


}
