package com.tigerobo.x.pai.api.auth.service;

import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.vo.UserReqVo;
import com.tigerobo.x.pai.api.auth.vo.UserVo;

import java.util.List;

/**
 * @description: 用户认证-用户信息接口定义
 * @modified By:
 * @version: $
 */
public interface UserService {

    // 修改
    Integer update(User user);


    UserVo getUserVo(final Integer id);


    List<User> getBaseUserByIds(List<Integer> userIds);

    Integer getIdByUuidCache(String uuid);

    boolean userIdExist(Integer id);

    List<User> getGroupMemberList(String groupUuid);

    User getFromCache(Integer userId);

    User getById(Integer userId);

    List<User> getByIds(List<Integer> userIds);

    User getUserByOther(Integer userId);

    User getCacheUserByOther(Integer userId);

    Integer getIdByUuId(String uuid);


}
