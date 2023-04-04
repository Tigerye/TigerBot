package com.tigerobo.x.pai.biz.micro.search;

import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class RpcValueFactory {
    @Value("${pai.search.hostUrl:}")
    String searchUrl = "http://pai-test.tigerobo.com/search-service";

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    public UserBase buildUserBase(){

        return buildUserBase(false);
    }


    public UserBase buildUserBase(boolean needRole){
        final Integer userId = ThreadLocalHolder.getUserId();

        final UserBase userBase = buildUserBase(userId);

        if (userBase!=null&&needRole){
            final Integer roleType = roleService.getRoleType(userId);
            userBase.setRole(roleType);
        }
        return userBase;
    }


    private UserBase buildUserBase(Integer userId) {
        if (userId !=null&& userId >0){
            return new UserBase(userId);
        }
        return null;
    }
}
