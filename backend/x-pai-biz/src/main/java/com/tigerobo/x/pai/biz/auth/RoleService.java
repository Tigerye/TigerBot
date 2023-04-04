package com.tigerobo.x.pai.biz.auth;

import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.enums.UserRoleTypeEnum;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RoleService {

    @Autowired
    private UserService userService;

    @Autowired
    RedisCacheService redisCacheService;

    public void checkAdmin() {
        Integer userId = ThreadLocalHolder.getUserId();
        User user = userService.getFromCache(userId);

        checkAdmin(user);
    }

    public void checkAdmin(User user) {
        if (user ==null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        Integer roleType = user.getRoleType();
        boolean hasRole = UserRoleTypeEnum.hasRole(roleType, UserRoleTypeEnum.SUPER_MAN);
        if (!hasRole){
            throw new AuthorizeException(ResultCode.USER_NO_PERMISSION);
        }
    }

    public boolean isAdmin(){


        Integer userId = ThreadLocalHolder.getUserId();
        return isAdmin(userId);
    }

    public boolean isAdmin(Integer userId) {
        if (userId == null|| userId <=0){
            return false;
        }

        Integer roleType = getRoleType(userId);
        return UserRoleTypeEnum.hasRole(roleType, UserRoleTypeEnum.SUPER_MAN);
    }

    public Integer getRoleType(Integer userId) {
        if (userId == null){
            return null;
        }
        final String key = userRoleKey(userId);
        Integer roleType = redisCacheService.getInteger(key);
        if (roleType==null){
            User user = userService.getFromCache(userId);
            if (user != null){
                roleType = user.getRoleType();
            }
        }
        if (roleType!=null){
            redisCacheService.set(key,String.valueOf(roleType),300);
        }

        return roleType;
    }


    private String userRoleKey(Integer id){
        return "p:user:role:"+id;
    }
}
