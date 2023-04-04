package com.tigerobo.x.pai.biz.pay.base;

import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.pay.dao.PaymentApiDetailDao;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiDetailPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
public class PaymentApiDetailBaseService {


    @Autowired
    private PaymentApiDetailDao paymentApiDetailDao;


    public List<PaymentApiDetailPo> getMonthDetails(int userId, int month, Integer startDay, Integer endDay) {


        int beginDay = month * 100 + 1;

        int lastDay = TimeUtil.getMonthLastDay(month);
        if (startDay == null || startDay < beginDay || startDay > lastDay) {
            startDay = beginDay;
        }
        if (endDay == null || endDay < beginDay || endDay > lastDay) {
            endDay = lastDay;
        }

        final List<PaymentApiDetailPo> userPeriodList = paymentApiDetailDao.getUserPeriodList(userId, startDay, endDay);

        return userPeriodList;
    }


    public void addList(List<PaymentApiDetailPo> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return;
        }
        pos.forEach(this::detailAddOrUpdate);
    }

    public void detailAddOrUpdate(PaymentApiDetailPo po) {
        final Integer userId = po.getUserId();
        final Integer day = po.getDay();
        final Integer agreementId = po.getAgreementId();
        final Integer consumeType = po.getConsumeType();

        final PaymentApiDetailPo dbPo = paymentApiDetailDao.getByKey(userId, agreementId, day, consumeType);

        if (dbPo != null) {
            po.setId(dbPo.getId());
            final boolean same = Objects.equals(po.getCallNum(), dbPo.getCallNum()) && Objects.equals(dbPo.getTotalFee(), po.getTotalFee());
            if (!same) {
                po.setStatus(null);
                paymentApiDetailDao.update(po);
            }
        } else {
            paymentApiDetailDao.add(po);
        }
    }

    public long countMonthPreTotal(int userId, String modelId, int day) {

        final int startDay = TimeUtil.getStartDay(day);

        final Integer preTotal = paymentApiDetailDao.sumUserCallNum(modelId, userId, startDay, day);

        return preTotal == null ? 0 : preTotal;
    }


}
