package com.tigerobo.x.pai.biz.pay.bill;

import com.tigerobo.x.pai.api.enums.PriceTypeEnum;
import com.tigerobo.x.pai.biz.pay.api.ApiModelSourceCallService;
import com.tigerobo.x.pai.biz.pay.api.ApiPriceService;
import com.tigerobo.x.pai.biz.pay.base.PaymentApiBillBaseService;
import com.tigerobo.x.pai.biz.pay.base.PaymentApiDetailBaseService;
import com.tigerobo.x.pai.biz.pay.payment.PaymentBillService;
import com.tigerobo.x.pai.biz.product.convert.SkuConvert;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.pay.dao.ApiAgreementDao;
import com.tigerobo.x.pai.dal.pay.entity.ApiAgreementPo;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiBillPo;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiDetailPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
@Deprecated
@Slf4j
@Service
public class ApiBillService {

    @Autowired
    private ApiAgreementDao apiAgreementDao;

    @Autowired
    private ApiModelSourceCallService apiModelSourceCallService;


    @Autowired
    private ApiPriceService apiPriceService;


    @Autowired
    private PaymentBillService paymentBillService;

    @Autowired
    private PaymentApiDetailBaseService paymentApiDetailBaseService;
    @Autowired
    private PaymentApiBillBaseService paymentApiBillBaseService;


    public void initUserBill(int day) {

        List<Integer> page = apiAgreementDao.getUserIds();
        if (CollectionUtils.isEmpty(page)) {
            return;
        }

        for (Integer userId : page) {
            try {
//                initUserApiDetailBill(userId, day);
                initMonthTotal(userId,TimeUtil.getMonthValue(day));
            } catch (Exception ex) {
                log.error("append apiBill err:userId:{},day-{}", userId, day, ex);
            }
        }

    }

    public void initUserApiDetailBill(int userId, int day) {

        final List<ApiAgreementPo> userAgreements = apiAgreementDao.getUserAgreements(userId);

        if (CollectionUtils.isEmpty(userAgreements)) {
            return;
        }

        List<String> existModelIds = new ArrayList<>();

        List<ApiAgreementPo> distinctAgreeList = new ArrayList<>();
        for (ApiAgreementPo userAgreement : userAgreements) {
            if (existModelIds.contains(userAgreement.getModelId())){
                continue;
            }
            existModelIds.add(userAgreement.getModelId());
            distinctAgreeList.add(userAgreement);
        }


        List<PaymentApiDetailPo> detailPos = distinctAgreeList.stream().map(agreementPo -> {
            return getUserAgreementDetail(agreementPo, userId, day);
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(detailPos)) {
            return;
        }

        paymentApiDetailBaseService.addList(detailPos);
//        paymentBillService.addMonthDetailPayment(billPo, detailPos);
    }

    public void initMonthTotal(int userId,int month){

        final List<ApiAgreementPo> userAgreements = apiAgreementDao.getUserAgreements(userId);
        if (CollectionUtils.isEmpty(userAgreements)){
            return;
        }

        PaymentApiBillPo billPo = new PaymentApiBillPo();
        billPo.setUserId(userId);
        billPo.setBillPeriod(month);
        billPo.setBillPeriod(month);

        final List<PaymentApiDetailPo> detailList = paymentApiDetailBaseService.getMonthDetails(
                userId, month, null,null);


        BigDecimal totalFee = BigDecimal.ZERO;
        Integer startDay = TimeUtil.getMonthFirstDay(month);
        Integer endDay = TimeUtil.getMonthLastDay(month);
        Integer today = TimeUtil.getDayValue(new Date());

        endDay = Math.min(endDay,today);
        if (!CollectionUtils.isEmpty(detailList)){
            for (PaymentApiDetailPo detailPo : detailList) {
                totalFee = totalFee.add(detailPo.getTotalFee());
            }
            Integer dbStartDay = detailList.stream().map(d->d.getDay()).min(Integer::compareTo).get();
            startDay = Math.max(dbStartDay,startDay);
        }
        billPo.setEndDay(endDay);
        billPo.setStartDay(startDay);
        billPo.setTotalFee(totalFee);
        paymentApiBillBaseService.addMonthBill(billPo);
    }

    private PaymentApiDetailPo getUserAgreementDetail(ApiAgreementPo agreementPo, int userId, int day) {

        final String modelId = agreementPo.getModelId();

        final long userCallNum = apiModelSourceCallService.getUserApiCallNum(userId, day, modelId);

        PaymentApiDetailPo detailPo = new PaymentApiDetailPo();
        detailPo.setUserId(userId);
        detailPo.setDay(day);

        detailPo.setAgreementId(agreementPo.getId());
        detailPo.setModelId(modelId);

        detailPo.setCallNum(userCallNum);

        detailPo.setItemPrice(agreementPo.getPrice());

        final Integer priceType = agreementPo.getPriceType();
        BigDecimal totalFee = null;
        if (PriceTypeEnum.DEFAULT_PRICE.getType().equals(priceType)) {
            final BigDecimal price = agreementPo.getPrice();
            if (price != null) {
                totalFee = price.multiply(new BigDecimal(userCallNum));
            }
        } else {
            final String properties = agreementPo.getProperties();
            final LinkedHashMap<Long, BigDecimal> priceRegionMap = SkuConvert.convertPriceRegionMap(properties);

            final long preTotal = paymentApiDetailBaseService.countMonthPreTotal(userId, agreementPo.getModelId(), day);
            totalFee = apiPriceService.calTotalPrice(preTotal, userCallNum, priceRegionMap, agreementPo.getUnit());
        }
        detailPo.setTotalFee(totalFee);
        return detailPo;

    }

}
