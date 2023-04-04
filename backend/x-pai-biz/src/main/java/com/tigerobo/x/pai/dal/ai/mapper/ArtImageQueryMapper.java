package com.tigerobo.x.pai.dal.ai.mapper;

import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.api.ai.req.interact.UserInteractPublicPageReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArtImageQueryMapper {

    List<Integer> queryNew(@Param("req") ArtImagePublicPageReq req,@Param("orderSort") String orderSort);

    List<Integer> queryAiInteract(@Param("req")UserInteractPublicPageReq req,@Param("bizTypes") List<Integer> bizTypes);

    void incrThumb(@Param("id")Integer id);

    void decrThumb(@Param("id")Integer id);
}
