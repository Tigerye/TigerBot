package com.tigerobo.x.pai.dal.admin.mapper;

import com.tigerobo.x.pai.api.admin.req.BigShotAdminReq;
import com.tigerobo.x.pai.api.admin.req.PubSiteAdminReq;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PubAdminQueryMapper {
    List<Integer> getBigShotPage(@Param("req") BigShotAdminReq req);

    List<Integer> getPubSitePage(@Param("req") PubSiteAdminReq req);


}
