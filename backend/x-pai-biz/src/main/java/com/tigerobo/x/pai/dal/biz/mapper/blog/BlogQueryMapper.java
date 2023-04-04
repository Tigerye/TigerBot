package com.tigerobo.x.pai.dal.biz.mapper.blog;

import com.tigerobo.x.pai.api.vo.biz.blog.BlogCategoryQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface BlogQueryMapper {
    List<Integer> query(@Param("req") BlogQueryVo req);
    List<Integer> queryMine(@Param("req") BlogQueryVo req);

    List<Integer> queryCategory(@Param("req") BlogCategoryQueryVo req);
    Integer getMaxId();
    Integer getMaxThirdId();

    Integer getTwitterMaxThirdId();
}
