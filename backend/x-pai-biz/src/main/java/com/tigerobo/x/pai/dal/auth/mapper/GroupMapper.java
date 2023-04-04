package com.tigerobo.x.pai.dal.auth.mapper;

import com.tigerobo.x.pai.dal.auth.entity.GroupDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@org.apache.ibatis.annotations.Mapper
public interface GroupMapper extends Mapper<GroupDo> {
    @Select(value = {"select * from `xpai-auth-group` where is_deleted = 0 and account = #{account}"})
    GroupDo selectByAccount(@Param("account") String account);

    @Select(value = {"select * from `xpai-auth-group` where is_deleted = 0 and name = #{name}"})
    List<GroupDo> selectByName(@Param("name") String name);
}