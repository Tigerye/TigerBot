package com.tigerobo.x.pai.biz.pay.api;

import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.pay.dao.ApiBalanceCardDao;
import com.tigerobo.x.pai.dal.pay.dao.PaymentApiDetailDao;
import com.tigerobo.x.pai.dal.pay.entity.ApiBalanceCardPo;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiDetailPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApiBalanceCardService {


    @Autowired
    private PaymentApiDetailDao paymentApiDetailDao;

    @Autowired
    private ApiBalanceCardDao apiBalanceCardDao;


    public ApiBalanceCardPo getUserModelRemainNum(Integer userId,String modelId,Long defaultFreeTotal){
        if (defaultFreeTotal == null||defaultFreeTotal<=0){
            return null;
        }
        final List<ApiBalanceCardPo> userModelFreeCards = apiBalanceCardDao.getUserModelFreeCards(userId, modelId);

        ApiBalanceCardPo target = new ApiBalanceCardPo();

        if (CollectionUtils.isEmpty(userModelFreeCards)){
            target.setTotalNum(defaultFreeTotal);
            target.setRemainNum(defaultFreeTotal);
        }else {
            Long total = 0L;
            Long remain = 0L;
            for (ApiBalanceCardPo userModelFreeCard : userModelFreeCards) {
                total += userModelFreeCard.getTotalNum();
                remain += userModelFreeCard.getRemainNum();
            }
            target.setTotalNum(total);


            if (remain>0){
                final List<Integer> cardIds = userModelFreeCards.stream().map(ApiBalanceCardPo::getId).collect(Collectors.toList());
                final Long todayDetailConsumeNum = getTodayDetailConsumeNum(userId, modelId,cardIds);
                if (todayDetailConsumeNum!=null&&todayDetailConsumeNum>0){

                    remain -= todayDetailConsumeNum;
                }
                remain = Math.max(0,remain);
            }
            target.setRemainNum(remain);
        }

        return target;
    }

    private Long getTodayDetailConsumeNum(Integer userId,String modelId,List<Integer> cardIds){

        if (CollectionUtils.isEmpty(cardIds)){
            return 0L;
        }

        final List<PaymentApiDetailPo> userDetails = paymentApiDetailDao.getUserDetails(userId, modelId, TimeUtil.getDayValue(new Date()), 1,1,cardIds);

        Long consumeNum = 0L;
        if (CollectionUtils.isEmpty(userDetails)){
            return consumeNum;
        }
        for (PaymentApiDetailPo userDetail : userDetails) {
            consumeNum += userDetail.getCallNum();
        }
        return consumeNum;
    }


    //todo ,事务处理
    @Transactional(value = "paiTm")
    public void cardSettlement(PaymentApiDetailPo detailPo) {

        final Integer cardId = detailPo.getAgreementId();
        if (cardId == null || cardId <= 0) {
            return;
        }


        final ApiBalanceCardPo po = apiBalanceCardDao.load(cardId);
        Validate.isTrue(po != null, "消费卡片不存在");

        final Long remainNum = po.getRemainNum();
        final Long callNum = detailPo.getCallNum();
        Validate.isTrue(remainNum != null && callNum != null, "次数为空");

        long afterRemainNum = remainNum - callNum;
        if (afterRemainNum < 0) {
            afterRemainNum = 0L;
        }

        ApiBalanceCardPo afterPo = new ApiBalanceCardPo();
        afterPo.setRemainNum(afterRemainNum);
        afterPo.setId(po.getId());
        apiBalanceCardDao.update(afterPo);

        PaymentApiDetailPo detailUpdate = new PaymentApiDetailPo();

        detailUpdate.setId(detailPo.getId());
        detailUpdate.setStatus(2);
        final int update = paymentApiDetailDao.update(detailUpdate);
        Validate.isTrue(update==1,"更新失败");
    }

}
