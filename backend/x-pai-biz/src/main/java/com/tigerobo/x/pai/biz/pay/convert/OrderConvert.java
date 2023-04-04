package com.tigerobo.x.pai.biz.pay.convert;

import com.tigerobo.x.pai.api.pay.dto.OrderDto;
import com.tigerobo.x.pai.api.pay.enums.OrderStatusEnum;
import com.tigerobo.x.pai.api.pay.enums.PayChannelTypeEnum;
import com.tigerobo.x.pai.dal.pay.entity.OrderPo;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class OrderConvert {


    public static List<OrderDto> po2dtos(List<OrderPo> pos){


        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.stream().map(OrderConvert::po2dto).collect(Collectors.toList());
    }

    public static OrderDto po2dto(OrderPo po){
        if (po == null){
            return null;
        }

        OrderDto dto = new OrderDto();

        dto.setOrderNo(po.getOrderNo());
        dto.setSkuId(po.getSkuId());
        dto.setUserId(po.getUserId());
        dto.setName(po.getName());
        dto.setAmount(po.getAmount());

        dto.setTotalPrice(po.getTotalPrice());

        OrderStatusEnum statusEnum = OrderStatusEnum.getByStatus(po.getStatus());
        dto.setStatus(po.getStatus());
        if (statusEnum!=null){
            dto.setStatusName(statusEnum.getName());
        }else {
            dto.setStatusName("");
        }

        PayChannelTypeEnum payChannelTypeEnum = PayChannelTypeEnum.getByType(po.getPayChannel());
        dto.setPayChannel(po.getPayChannel());
        if (payChannelTypeEnum!=null){
            dto.setPayChannelName(payChannelTypeEnum.getName());
        }else {
            dto.setPayChannelName("");
        }

        dto.setPaymentNo(po.getPaymentNo());
        dto.setPayTime(po.getPayTime());
        return dto;

    }
}
