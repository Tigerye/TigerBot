package com.tigerobo.x.pai.dal.auth.mapper;

import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
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
public interface UserMapper extends Mapper<UserDo> {

    @Select(value = {"select * from `xpai-auth-user` where is_deleted = 0 and account = #{account}"})
    UserDo selectByAccount(@Param("account") String account);

    @Select(value = {"select * from `xpai-auth-user` where is_deleted = 0 and mobile = #{mobile}"})
    UserDo selectByMobile(@Param("mobile") String mobile);

    @Select(value = {"select * from `xpai-auth-user` where is_deleted = 0 and wechat = #{wechat}"})
    UserDo selectByWechat(@Param("wechat") String wechat);

    @Select(value = {"select * from `xpai-auth-user` where is_deleted = 0 and name = #{name}"})
    List<UserDo> selectByName(@Param("name") String name);

    @Select(value = {"select * from `xpai-auth-user` where is_deleted = 0 and email = #{email}"})
    UserDo selectByEmail(@Param("email") String email);

    @Select(value = {"select * from `xpai-auth-user` where is_deleted = 0 and (account = #{user.account} or mobile = #{user.mobile} or wechat = #{user.wechat} or email = #{user.email})"})
    UserDo selectByUser(@Param("user") User user);

    @Select(value = {"select id,roleType from `xpai-auth-user` where uuid = #{uuid} and is_deleted = 0 limit 1"})
    UserDo selectByUuid(@Param("uuid") String uuid);
}
