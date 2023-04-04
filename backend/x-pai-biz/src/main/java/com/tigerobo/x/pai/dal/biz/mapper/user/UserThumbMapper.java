package com.tigerobo.x.pai.dal.biz.mapper.user;

import com.tigerobo.x.pai.dal.biz.entity.BizCountPo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserThumbPo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface UserThumbMapper extends Mapper<UserThumbPo> {

    @Select("select biz_id,count(1) n from user_thumb where biz_type = #{type} and is_deleted=0 group by biz_id")
    List<BizCountPo> groupBizCount(@Param("type") Integer type);
}
