package com.tigerobo.x.pai.biz.pay.convert;

import com.tigerobo.x.pai.api.pay.enums.OrderStatusEnum;
import com.tigerobo.x.pai.api.pay.vo.api.ApiBillVo;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiBillPo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApiBillConvert {


    public static List<ApiBillVo> po2vos(List<PaymentApiBillPo> pos){

        if (CollectionUtils.isEmpty(pos)){
            return new ArrayList<>();
        }
        return pos.stream().map(ApiBillConvert::po2vo).collect(Collectors.toList());
    }


    public static ApiBillVo po2vo(PaymentApiBillPo po){

        ApiBillVo vo = new ApiBillVo();

        vo.setId(po.getId());
        final Integer billPeriod = po.getBillPeriod();
        vo.setBillPeriod(billPeriod);

        int year = billPeriod/100;
        int month = billPeriod%100;
        String periodName = year+"年"+month+"月";
        vo.setPeriodName(periodName);
        vo.setStartDay(po.getStartDay());
        vo.setEndDay(po.getEndDay());
        vo.setTotalFee(po.getTotalFee());
        vo.setOrderNo(po.getOrderNo());
        vo.setStatus(po.getStatus());

        final OrderStatusEnum statusEnum = OrderStatusEnum.getByStatus(po.getStatus());
        String statusName = statusEnum == null?"":statusEnum.getName();
        vo.setStatusName(statusName);

        vo.setPayTime(po.getPayTime());
        return vo;
    }

}
