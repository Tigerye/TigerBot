package com.tigerobo.x.pai.dal.biz.mapper.github;

import com.tigerobo.x.pai.api.dto.admin.github.req.RepoAdminReq;
import com.tigerobo.x.pai.api.vo.github.RepoQueryReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface GithubMapper {
    Integer getRepoMaxThirdId();
    Integer getUserMaxThirdId();

    List<Integer> queryRepoIds(@Param("req")RepoQueryReq req);

    List<Integer> adminQueryRepoIds(@Param("req") RepoAdminReq req);
}
