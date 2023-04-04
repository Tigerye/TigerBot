package com.tigerobo.x.pai.dal.pay.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApiAgreementQueryMapper {

    @Select(value = "select user_id from api_agreement group by user_id")
    List<Integer> getUserIds();
}
