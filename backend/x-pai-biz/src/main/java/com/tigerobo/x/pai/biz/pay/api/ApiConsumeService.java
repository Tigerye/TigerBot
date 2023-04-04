package com.tigerobo.x.pai.biz.pay.api;

import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.api.enums.PriceTypeEnum;
import com.tigerobo.x.pai.biz.pay.base.PaymentApiDetailBaseService;
import com.tigerobo.x.pai.biz.pay.bill.ApiBillService;
import com.tigerobo.x.pai.biz.product.convert.SkuConvert;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.dao.call.ModelSourceCallDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import com.tigerobo.x.pai.dal.biz.entity.call.ModelSourceCallPo;
import com.tigerobo.x.pai.dal.pay.dao.ApiAgreementDao;
import com.tigerobo.x.pai.dal.pay.dao.ApiBalanceCardDao;
import com.tigerobo.x.pai.dal.pay.entity.ApiAgreementPo;
import com.tigerobo.x.pai.dal.pay.entity.ApiBalanceCardPo;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiDetailPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApiConsumeService {

    @Autowired
    private ModelSourceCallDao modelSourceCallDao;

    @Autowired
    private ApiAgreementDao apiAgreementDao;

    @Autowired
    private ApiBalanceCardDao apiBalanceCardDao;

    @Autowired
    private ApiDao apiDao;
    @Autowired
    private PaymentApiDetailBaseService paymentApiDetailBaseService;
    @Autowired
    private ApiPriceService apiPriceService;

    @Autowired
    private ApiBillService apiBillService;



    public void sumDetailBillOnDay(int day) {

        final List<Integer> userIds = modelSourceCallDao.getDayUserIdsForConsume(day);
        if (CollectionUtils.isEmpty(userIds)){
            return;
        }
        for (Integer userId : userIds) {
            try {
                calUserModelCall(day, userId);
                apiBillService.initMonthTotal(userId, TimeUtil.getMonthValue(day));
            }catch (Exception ex){
                log.error("dealDay,day:{},userId:{}",day,userId,ex);
            }
        }
    }

    @Transactional(value = "paiTm")
    public void calUserModelCall(int day, int userId) {

        final List<ModelSourceCallPo> userDayCallList = modelSourceCallDao
                .getUserDayCallList(day, userId, null, Arrays.asList(2, 4)
                        , ModelCallTypeEnum.APP.getType());

        if (CollectionUtils.isEmpty(userDayCallList)) {
            return;
        }

        Map<String, Long> modelCallNumMap = new LinkedHashMap<>();

        for (ModelSourceCallPo callPo : userDayCallList) {
            final String modelId = callPo.getModelId();

            Long callNumTotal = modelCallNumMap.computeIfAbsent(modelId, k -> 0L);

            final Integer num = callPo.getNum();
            callNumTotal += num;
            modelCallNumMap.put(modelId, callNumTotal);
        }

        for (Map.Entry<String, Long> entry : modelCallNumMap.entrySet()) {
            final String modelId = entry.getKey();
            final Long callNum = entry.getValue();
            dealUserModel(userId, modelId, callNum, day);
        }

    }

    private void dealUserModel(int userId, String modelId, long callNum, int day) {
        if (callNum == 0) {
            return;
        }
        final List<PaymentApiDetailPo> apiDetailPos = doDealUserModel(userId, modelId, callNum, day);
        if (CollectionUtils.isEmpty(apiDetailPos)) {
            return;
        }
        paymentApiDetailBaseService.addList(apiDetailPos);
    }

    private List<PaymentApiDetailPo> doDealUserModel(int userId, String modelId, long callNum, int day) {
        long waitDealNum = callNum;
        List<PaymentApiDetailPo> apiDetailPos = new ArrayList<>();

        waitDealNum = balanceCardConsume(userId, modelId, waitDealNum, apiDetailPos);
        if (waitDealNum > 0) {
            waitDealNum = agreementConsume(apiDetailPos, userId, modelId, waitDealNum, day);
        }

        if (waitDealNum > 0) {
            PaymentApiDetailPo excessPo = new PaymentApiDetailPo();
            excessPo.setConsumeType(2);
            excessPo.setCallNum(waitDealNum);
            excessPo.setAgreementId(0);
            apiDetailPos.add(excessPo);
        }
        if (apiDetailPos.size() > 0) {
            for (PaymentApiDetailPo detailPo : apiDetailPos) {
                detailPo.setUserId(userId);
                detailPo.setDay(day);
                detailPo.setModelId(modelId);
                if (detailPo.getTotalFee() == null) {
                    detailPo.setTotalFee(BigDecimal.ZERO);
                }
            }
        }
        return apiDetailPos;
    }

    private long balanceCardConsume(int userId, String modelId, long waitDealNum,
                                    List<PaymentApiDetailPo> apiDetailPos) {
        List<ApiBalanceCardPo> userCards = apiBalanceCardDao.getUserCards(userId, modelId);

        if (CollectionUtils.isEmpty(userCards)) {
            final ApiDo apiDo = apiDao.getByModelUuid(modelId);
            if (apiDo != null && apiDo.getOriginCallCount() != null && apiDo.getOriginCallCount() > 0) {
                ApiBalanceCardPo freeCard = new ApiBalanceCardPo();
                freeCard.setUserId(userId);
                freeCard.setModelId(modelId);
                freeCard.setTotalNum(apiDo.getOriginCallCount().longValue());
                freeCard.setRemainNum(apiDo.getOriginCallCount().longValue());
                freeCard.setStatus(1);
                freeCard.setCardType(1);
                final int add = apiBalanceCardDao.add(freeCard);
                if (add != 1 || freeCard.getId() == null) {
                    log.error("userId:{},modelId:{},添加免费额度失败", userId, modelId);
                    throw new IllegalArgumentException(userId + "添加免费额度失败");
                }
                userCards = new ArrayList<>();
                userCards.add(freeCard);
            }
        }


        if (!CollectionUtils.isEmpty(userCards)) {
            final List<ApiBalanceCardPo> effectCards = userCards.stream()
                    .filter(u -> u.getRemainNum() > 0 && u.getStatus() == 1)
                    .collect(Collectors.toList());
            if (effectCards.size() > 0) {

                for (ApiBalanceCardPo effectCard : effectCards) {
                    final PaymentApiDetailPo detailPo = buildCardDetailPo(effectCard, waitDealNum);
                    waitDealNum = waitDealNum - detailPo.getCallNum();
                    apiDetailPos.add(detailPo);
                    if (waitDealNum <= 0) {
                        break;
                    }
                }
            }
        }
        return waitDealNum;
    }


    private long agreementConsume(List<PaymentApiDetailPo> apiDetailPos, int userId, String modelId, long waitDealNum,
                                  int day) {

        final ApiAgreementPo agreementPo = apiAgreementDao.getUserModelAgreement(userId, modelId);
        if (agreementPo == null) {
            return waitDealNum;
        }


        final PaymentApiDetailPo userAgreementDetail = getUserAgreementDetail(agreementPo, userId, day, waitDealNum);
        if (userAgreementDetail!=null){
            apiDetailPos.add(userAgreementDetail);
        }
        return 0;
    }

    private PaymentApiDetailPo getUserAgreementDetail(ApiAgreementPo agreementPo, int userId, int day, long waitDealNum) {

        final String modelId = agreementPo.getModelId();
        PaymentApiDetailPo detailPo = new PaymentApiDetailPo();
        detailPo.setUserId(userId);
        detailPo.setDay(day);

        detailPo.setAgreementId(agreementPo.getId());
        detailPo.setModelId(modelId);
        detailPo.setCallNum(waitDealNum);
        detailPo.setConsumeType(0);
        detailPo.setStatus(0);

        final Integer priceType = agreementPo.getPriceType();
        BigDecimal totalFee = null;
        if (PriceTypeEnum.DEFAULT_PRICE.getType().equals(priceType)) {
            final BigDecimal price = agreementPo.getPrice();
            if (price != null) {
                totalFee = price.multiply(new BigDecimal(waitDealNum));
            }
        } else {
            final String properties = agreementPo.getProperties();
            final LinkedHashMap<Long, BigDecimal> priceRegionMap = SkuConvert.convertPriceRegionMap(properties);

            final long preTotal = paymentApiDetailBaseService.countMonthPreTotal(userId, agreementPo.getModelId(), day);
            totalFee = apiPriceService.calTotalPrice(preTotal, waitDealNum, priceRegionMap, agreementPo.getUnit());
        }
        detailPo.setTotalFee(totalFee);
        return detailPo;

    }

    private PaymentApiDetailPo buildCardDetailPo(ApiBalanceCardPo effectCard, long waitDealNum) {

        final Long remainNum = effectCard.getRemainNum();
        long consumeNum = Math.min(remainNum, waitDealNum);

        PaymentApiDetailPo detailPo = new PaymentApiDetailPo();
        detailPo.setConsumeType(1);
        detailPo.setCallNum(consumeNum);
        detailPo.setStatus(1);
        detailPo.setAgreementId(effectCard.getId());
        return detailPo;

    }

}
