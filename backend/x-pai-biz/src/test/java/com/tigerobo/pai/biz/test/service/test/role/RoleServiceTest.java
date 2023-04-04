package com.tigerobo.pai.biz.test.service.test.role;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.auth.RoleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RoleServiceTest extends BaseTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void typeTest(){
        final Integer roleType = roleService.getRoleType(3);
        final Integer roleType1 = roleService.getRoleType(null);

        System.out.println(roleType+"\t"+roleType1);
    }
}
