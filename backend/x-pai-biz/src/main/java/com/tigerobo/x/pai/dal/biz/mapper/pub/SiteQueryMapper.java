package com.tigerobo.x.pai.dal.biz.mapper.pub;

import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.api.vo.biz.pub.BigShotQueryReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteQueryMapper {

    List<Integer> getBigShotPage(@Param("req") BigShotQueryReq req);

    List<Integer> getSitePage(@Param("req") BigShotQueryReq req);

    List<Integer> getBloggerUserIdPage(@Param("req") UserPageReq req);
}
