package com.tigerobo.x.pai.biz.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class ProductUnitUtil {
    private static final Long YI = 100000000L;

    public static BigDecimal getAmount(BigDecimal value,String unit){

        final BigDecimal unitMulAmount = getUnitAmount(unit);
        return value.multiply(unitMulAmount);
    }

    public static Long getAmount(Long value,String unit){

        final BigDecimal unitMulAmount = getUnitAmount(unit);
        return new BigDecimal(value).multiply(unitMulAmount).longValue();
    }
    public static BigDecimal getUnitAmount(String unit){
        BigDecimal num = new BigDecimal(1);
        if (StringUtils.isEmpty(unit)){
            return num;
        }
        for (int i = 0; i < unit.length(); i++) {
            Long unitValue = getUnitValue(unit.substring(i, i + 1));
            if (unitValue != null) {
                num = num.multiply(new BigDecimal(unitValue));
            }
        }
        return num;
    }


    private static Long getUnitValue(String unit){
        if ("亿".equals(unit)){
            return YI;
        }
        if ("万".equals(unit)){
            return 10000L;
        }
        if ("千".equals(unit)){
            return 1000L;
        }
        if ("百".equals(unit)){
            return 100L;
        }
        if ("十".equals(unit)){
            return 10L;
        }
        return null;
    }

}
