package com.tigerobo.pai.biz.test.pay.api;

import com.tigerobo.x.pai.api.entity.Pair;
import com.tigerobo.x.pai.biz.pay.api.ApiPriceService;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

public class ApiPriceServiceTest {

    @Test
    public void priceCalTest(){

        ApiPriceService service= new ApiPriceService();

        LinkedHashMap<Long, BigDecimal> periodPriceMap = new LinkedHashMap<>();

        periodPriceMap.put(0L,new BigDecimal(8));
        periodPriceMap.put(10L,new BigDecimal("7.5"));
        periodPriceMap.put(100L,new BigDecimal("7"));
        periodPriceMap.put(500L,new BigDecimal("6.5"));
        periodPriceMap.put(1000L,new BigDecimal("6"));
        periodPriceMap.put(5000L,new BigDecimal("5.5"));
        periodPriceMap.put(10000L,new BigDecimal("5"));


        long pre = 0L;
        long cal = 123000;

        final BigDecimal bigDecimal = service.calTotalPrice(pre, cal, periodPriceMap, "万次");
        System.out.println(bigDecimal);

        final List<Pair<Long, BigDecimal>> list = service.getCalPairs(pre, cal, periodPriceMap, "万次");

        for (Pair<Long, BigDecimal> pair : list) {
            System.out.println(pair.getV1()+"|"+pair.getV2());
        }


    }
}
