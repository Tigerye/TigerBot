package com.tigerobo.x.pai.biz.pay.api;

import com.tigerobo.x.pai.api.entity.Pair;
import com.tigerobo.x.pai.biz.utils.ProductUnitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
public class ApiPriceService {


    public BigDecimal calTotalPrice(long preTotalNum, long calNum,
                                    LinkedHashMap<Long, BigDecimal> numPeriodPriceMap, String unit) {
        if (CollectionUtils.isEmpty(numPeriodPriceMap)) {
            return BigDecimal.ZERO;
        }

        List<Pair<Long, BigDecimal>> numPriceList = getCalPairs(preTotalNum, calNum, numPeriodPriceMap, unit);

        BigDecimal totalFee = BigDecimal.ZERO;

        final BigDecimal unitAmount = ProductUnitUtil.getUnitAmount(unit);
        for (Pair<Long, BigDecimal> pair : numPriceList) {
            final BigDecimal num = new BigDecimal(pair.getV1());
            final BigDecimal price = pair.getV2();

            final BigDecimal periodFee = num.multiply(price).divide(unitAmount, 4, RoundingMode.HALF_UP);
            totalFee = totalFee.add(periodFee);
        }
        return totalFee;

    }
    public List<Pair<Long, BigDecimal>> getCalPairs(long preTotalNum, long calNum, LinkedHashMap<Long, BigDecimal> numPeriodPriceMap, String unit) {
        final ArrayList<Long> periods = new ArrayList<>(numPeriodPriceMap.keySet());
        long total = preTotalNum + calNum;
        final int totalPeriodIndex = getMaxPeriodIndex(total, periods, unit);

        final int prePeriodIndex = getMaxPeriodIndex(preTotalNum, periods, unit);

        List<Pair<Long, BigDecimal>> numPriceList = new ArrayList<>();
        long tempTotal = total;
        for (int i = Math.max(0, totalPeriodIndex); i>=0&&i >= prePeriodIndex; i--) {
            final Long period = periods.get(i);
            final Long amount = ProductUnitUtil.getAmount(period, unit);

            long needCalNum = 0;
            if (preTotalNum > amount) {
                needCalNum = tempTotal - prePeriodIndex;
            } else {
                needCalNum = tempTotal - amount;
            }

            tempTotal = amount;
            final BigDecimal price = numPeriodPriceMap.get(period);
            if (needCalNum > 0) {
                numPriceList.add(new Pair<>(needCalNum, price));
            }
        }
        return numPriceList;
    }

    private int getMaxPeriodIndex(long total, ArrayList<Long> periods, String unit) {

        for (int i = periods.size() - 1; i >= 0; i--) {

            final Long num = periods.get(i);
            if (num == null) {
                continue;
            }

            final Long amount = ProductUnitUtil.getAmount(num, unit);

            if (total > amount) {
                return i;
            }
        }
        return -1;

    }


}
