package com.tigerobo.x.pai.dal.admin.mapper;

import com.tigerobo.x.pai.api.admin.req.UserCommitSiteAdminReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface UserCommitSiteAdminMapper {
    List<Integer> query(@Param("req") UserCommitSiteAdminReq req);
}
