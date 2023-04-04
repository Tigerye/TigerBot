package com.tigerobo.x.pai.biz.pay.api.manage;

import com.tigerobo.x.pai.biz.pay.api.ApiBalanceCardService;
import com.tigerobo.x.pai.biz.pay.api.ApiConsumeService;
import com.tigerobo.x.pai.biz.pay.bill.ApiBillService;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.biz.dao.call.ModelSourceCallDao;
import com.tigerobo.x.pai.dal.pay.dao.PaymentApiDetailDao;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiDetailPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
public class ApiBillManage {


    @Autowired
    private ApiBalanceCardService apiBalanceCardService;

    @Autowired
    private PaymentApiDetailDao paymentApiDetailDao;
    @Autowired
    private ModelSourceCallDao modelSourceCallDao;

    @Autowired
    private ApiConsumeService apiConsumeService;
    @Autowired
    private ApiBillService apiBillService;

    public void billDetailTask(int day) {

        final List<Integer> userIds = modelSourceCallDao.getDayUserIdsForConsume(day);
        if (CollectionUtils.isEmpty(userIds)){
            return;
        }
        for (Integer userId : userIds) {
            try {
                apiConsumeService.calUserModelCall(day, userId);
                apiBillService.initMonthTotal(userId, TimeUtil.getMonthValue(day));
            }catch (Exception ex){
                log.error("dealDay,day:{},userId:{}",day,userId,ex);
            }
        }
    }

    public void paymentDetailBillSettlement(int day) {

        final List<Integer> userIds = paymentApiDetailDao.getCardConsumeWaitDealUserIdsByDay(day);
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        for (Integer userId : userIds) {
            final List<PaymentApiDetailPo> userDetails = paymentApiDetailDao.getUserDetails(userId, day, 1, 1);
            if (!CollectionUtils.isEmpty(userDetails)){
                for (PaymentApiDetailPo userDetail : userDetails) {
                    try {
                        apiBalanceCardService.cardSettlement(userDetail);
                    }catch (Exception ex){
                        log.error("detail,id:{},userId:{},modelId:{},day:{}",userDetail.getId(),userDetail.getUserId(),
                                userDetail.getModelId(),userDetail.getDay(),ex);
                    }
                }
            }
        }
    }

}
