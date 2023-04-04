package com.tigerobo.x.pai.dal.ai.mapper;

import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpatioActionQueryMapper {

    List<Integer> query(@Param("req") ArtImagePublicPageReq req);
}
