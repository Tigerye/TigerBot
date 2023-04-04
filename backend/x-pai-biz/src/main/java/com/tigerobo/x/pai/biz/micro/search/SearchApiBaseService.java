package com.tigerobo.x.pai.biz.micro.search;

import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.base.EnvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SearchApiBaseService {


    @Autowired
    RestTemplate restTemplate;

    //    @Value("${pai.search.host:http://pai-test.tigerobo.com/search-service}")
    @Value("${pai.search.hostUrl:}")
    String searchUrl;
    @Autowired
    EnvService envService;

    @Autowired
    private RoleService roleService;

    UserBase buildUserBase(Integer userId) {

        if (userId != null && userId > 0) {
            final UserBase userBase = new UserBase(userId);
            final Integer roleType = roleService.getRoleType(userId);
            userBase.setRole(roleType);
            return userBase;
        }
        return null;
    }
}
