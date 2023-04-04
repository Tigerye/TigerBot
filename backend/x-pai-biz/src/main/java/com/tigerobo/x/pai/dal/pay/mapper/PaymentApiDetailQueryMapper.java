package com.tigerobo.x.pai.dal.pay.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface PaymentApiDetailQueryMapper {

    Integer sumCallNum(@Param("modelId") String modelId,
                       @Param("userId") int userId,
                       @Param("startDay") int startDay,
                       @Param("endDay") int endDay);

    List<Integer> getCardConsumeWaitDealUserIdsByDay(@Param("day")Integer day);
}
