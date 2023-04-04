package com.tigerobo.x.pai.biz.pay.payment;

import com.tigerobo.x.pai.api.pay.vo.api.ApiBillDetailStatisticVo;
import com.tigerobo.x.pai.api.pay.vo.api.ApiBillVo;
import com.tigerobo.x.pai.biz.pay.OrderService;
import com.tigerobo.x.pai.biz.pay.base.PaymentApiBillBaseService;
import com.tigerobo.x.pai.biz.pay.base.PaymentApiDetailBaseService;
import com.tigerobo.x.pai.biz.pay.convert.ApiBillConvert;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.pay.dao.ApiAgreementDao;
import com.tigerobo.x.pai.dal.pay.dao.PaymentApiBillDao;
import com.tigerobo.x.pai.dal.pay.entity.ApiAgreementPo;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiBillPo;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiDetailPo;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentBillService {


    @Autowired
    private PaymentApiBillDao paymentApiBillDao;
    @Autowired
    private PaymentApiBillBaseService paymentApiBillBaseService;

    @Autowired
    private PaymentApiDetailBaseService paymentApiDetailBaseService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ApiAgreementDao apiAgreementDao;


    public List<ApiBillVo> getUserBillList(Integer userId) {

        //todo
        final List<PaymentApiBillPo> userBills = paymentApiBillDao.getUserBills(userId);

        return ApiBillConvert.po2vos(userBills);
    }

    public ApiBillVo getUserPreApiBillList(Integer userId) {
        final int monthValue = TimeUtil.getMonthValue(DateUtils.addMonths(new Date(), -1));
        return getUserMonthBill(userId, monthValue);
    }

    public ApiBillVo getUserCurrentMonthBill(Integer userId) {
        final int monthValue = TimeUtil.getMonthValue(new Date());
        return getUserMonthBill(userId, monthValue);
    }


    public ApiBillVo getUserMonthBill(Integer userId, int month) {
        PaymentApiBillPo po = paymentApiBillDao.getByKey(userId, month);
        if (po == null) {
            return buildEmptyBill(month);
        }
        return ApiBillConvert.po2vo(po);
    }

    private ApiBillVo buildEmptyBill(int month) {
        PaymentApiBillPo po = new PaymentApiBillPo();
        po.setId(-1);
        po.setBillPeriod(month);
        po.setTotalFee(BigDecimal.ZERO);
        po.setStatus(0);
        final ApiBillVo vo = ApiBillConvert.po2vo(po);
        return vo;
    }

    public ApiBillDetailStatisticVo getBillStatistic(Integer userId, Integer month) {
        ApiBillDetailStatisticVo statisticVo = new ApiBillDetailStatisticVo();
        PaymentApiBillPo po = paymentApiBillDao.getByKey(userId, month);
        if (po == null) {
            final ApiBillVo apiBillVo = buildEmptyBill(month);
            statisticVo.setPeriodBill(apiBillVo);
            return statisticVo;
        }
        final ApiBillVo billVo = ApiBillConvert.po2vo(po);
        statisticVo.setPeriodBill(billVo);
        if (billVo.getId() == null || billVo.getId() < 0) {
            return statisticVo;
        }

        final List<PaymentApiDetailPo> detailList = paymentApiDetailBaseService.getMonthDetails(
                userId, month, billVo.getStartDay(), billVo.getEndDay());

        if (!CollectionUtils.isEmpty(detailList)) {
            final List<Integer> agreementIds = detailList.stream()
                    .map(d -> d.getAgreementId()).distinct().collect(Collectors.toList());


            final List<ApiAgreementPo> agreementPos = apiAgreementDao.load(agreementIds);

            LinkedHashMap<Integer, ApiAgreementPo> idAgreementMap = new LinkedHashMap<>();

            if (!CollectionUtils.isEmpty(agreementPos)) {
                for (ApiAgreementPo agreementPo : agreementPos) {
                    idAgreementMap.put(agreementPo.getId(), agreementPo);
                }

            }

            final List<ApiBillDetailStatisticVo.ModelBill> modelBills = buildModelBill(agreementPos, detailList);
            statisticVo.setModelBillList(modelBills);
            statisticVo.setDayBillList(buildDayBill(billVo, idAgreementMap, detailList));
        }

        return statisticVo;
    }


    private List<ApiBillDetailStatisticVo.ModelBill> buildModelBill(List<ApiAgreementPo> agreementPos
            , List<PaymentApiDetailPo> userPeriodList) {
        List<ApiBillDetailStatisticVo.ModelBill> modelBills = new ArrayList<>();
        for (ApiAgreementPo agreementPo : agreementPos) {
            final Integer agreementId = agreementPo.getId();
            BigDecimal totalFee = BigDecimal.ZERO;
            Long callNum = 0L;
            for (PaymentApiDetailPo detailPo : userPeriodList) {
                if (detailPo.getAgreementId().equals(agreementId)) {
                    totalFee = totalFee.add(detailPo.getTotalFee());
                    callNum += detailPo.getCallNum();
                }
            }
            ApiBillDetailStatisticVo.ModelBill bill = new ApiBillDetailStatisticVo.ModelBill();
            bill.setAgreementId(agreementId);
            bill.setCallNum(callNum);
            bill.setTotalFee(totalFee);
            bill.setModelName(agreementPo.getName());

            modelBills.add(bill);
        }
        return modelBills;
    }


    private List<ApiBillDetailStatisticVo.DayBill> buildDayBill(ApiBillVo billVo
            , LinkedHashMap<Integer, ApiAgreementPo> idAgreementMap, List<PaymentApiDetailPo> apiDetailPos) {

        if (CollectionUtils.isEmpty(apiDetailPos)) {
            return new ArrayList<>();
        }

        LinkedHashMap<Integer, List<PaymentApiDetailPo>> dayDetailMap = new LinkedHashMap<>();

        for (PaymentApiDetailPo apiDetailPo : apiDetailPos) {
            final Integer day = apiDetailPo.getDay();

            final List<PaymentApiDetailPo> dayDetailPos = dayDetailMap.computeIfAbsent(day, k -> new ArrayList<>());
            dayDetailPos.add(apiDetailPo);
        }


        final Integer month = billVo.getBillPeriod();

        int beginDay = month * 100 + 1;

        int lastDay = TimeUtil.getMonthLastDay(month);
        Integer startDay = billVo.getStartDay();
        Integer endDay = billVo.getEndDay();
        if (startDay == null || startDay < beginDay || startDay > lastDay) {
            startDay = beginDay;
        }
        if (endDay == null || endDay < beginDay || endDay > lastDay) {
            endDay = lastDay;
        }

        List<ApiBillDetailStatisticVo.DayBill> dayBills = new ArrayList<>();
        for (int day = startDay; day <= endDay; day++) {
            ApiBillDetailStatisticVo.DayBill dayBill = new ApiBillDetailStatisticVo.DayBill();
            dayBills.add(dayBill);
            dayBill.setDay(day);
            final Date dayFormat = TimeUtil.convertDay(day);
            dayBill.setDayFormat(dayFormat);
            BigDecimal dayTotalFee = BigDecimal.ZERO;

            List<ApiBillDetailStatisticVo.DayBillDetail> dayBillDetails = new ArrayList<>();
            dayBill.setDetailList(dayBillDetails);
            final List<PaymentApiDetailPo> dayList = dayDetailMap.get(day);

            if (!CollectionUtils.isEmpty(dayList)) {
                for (PaymentApiDetailPo detailPo : dayList) {
                    ApiBillDetailStatisticVo.DayBillDetail dayBillDetail = new ApiBillDetailStatisticVo.DayBillDetail();
                    dayBillDetail.setAgreementId(detailPo.getAgreementId());
                    dayBillDetail.setCallNum(detailPo.getCallNum());
                    dayBillDetail.setTotalFee(detailPo.getTotalFee());
                    dayBillDetails.add(dayBillDetail);
                    final ApiAgreementPo apiAgreementPo = idAgreementMap.get(detailPo.getAgreementId());
                    if (apiAgreementPo != null) {
                        dayBillDetail.setModelName(apiAgreementPo.getModelName());
                    }
                    dayTotalFee = dayTotalFee.add(detailPo.getTotalFee());
                }
            }
            dayBill.setTotalFee(dayTotalFee);
        }
        return dayBills;
    }


    public void paySuccess(String orderNo) {

        if (StringUtils.isEmpty(orderNo)) {
            return;
        }
        paymentApiBillDao.updatePaySuccess(orderNo, new Date());
    }

    public void bill2order(int id) {

        final PaymentApiBillPo load = paymentApiBillDao.load(id);
        bill2order(load);

    }


    public void bill2order(PaymentApiBillPo po) {

        Validate.isTrue(po.getStatus() != null && po.getStatus() != 2, "账单已支付");
        final Long orderNo = orderService.createBillOrder(po);
        Validate.isTrue(orderNo != null, "生成订单失败");
        PaymentApiBillPo update = new PaymentApiBillPo();

        update.setId(po.getId());
        update.setOrderNo(String.valueOf(orderNo));
        update.setStatus(1);
        paymentApiBillDao.update(update);
    }
}
