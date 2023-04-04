package com.tigerobo.x.pai.dal.biz.mapper;

import com.tigerobo.x.pai.api.vo.biz.DemandQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@org.apache.ibatis.annotations.Mapper
public interface DemandQueryMapper {
//
//    @Select("select id from `xpai-biz-demand` where create_by = #{userId} and is_deleted=0 " +
//            "union select id from `xpai-biz-demand` where scope = 90 and is_deleted=0")
    List<Integer> getDemandIdList(@Param("userId") String userId,
                                  @Param("query") DemandQueryVo query,
                                  @Param("groupUuids") List<String> groupUuids,
                                  @Param("creators") List<String> creators);

    int countUserViewDemand(@Param("userId") String userId,
                                  @Param("query") DemandQueryVo query);
}
