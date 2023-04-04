package com.tigerobo.x.pai.biz.micro.search;

import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseUserCombineService {


    @Autowired
    private RoleService roleService;
    protected UserBase buildUserBase() {
        final Integer userId = ThreadLocalHolder.getUserId();

        return buildUserBase(userId);
    }

    protected UserBase buildUserBase(Integer userId) {
        if (userId != null && userId > 0) {
            final UserBase userBase = new UserBase(userId);
            final Integer roleType = roleService.getRoleType(userId);
            userBase.setRole(roleType);
            return userBase;
        }
        return null;
    }
}
