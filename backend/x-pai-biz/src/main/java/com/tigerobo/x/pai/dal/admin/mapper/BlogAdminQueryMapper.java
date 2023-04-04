package com.tigerobo.x.pai.dal.admin.mapper;

import com.tigerobo.x.pai.api.admin.req.BlogAdminQueryReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlogAdminQueryMapper {
    List<Integer> query(@Param("req") BlogAdminQueryReq req);
}
