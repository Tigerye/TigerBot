package com.tigerobo.x.pai.dal.biz.mapper.call;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SourceCallQueryMapper {

    List<Integer> getDayUserIds(@Param("day") Integer day);
}
