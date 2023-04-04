package com.tigerobo.pai.admin;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.admin.req.user.UserSearchReq;
import com.tigerobo.x.pai.api.dto.user.UserDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.admin.user.UserAdminService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAdminServiceTest extends AdminBaseTest {

    @Autowired
    private UserAdminService userAdminService;

    @Test
    public void queryTest(){

        UserSearchReq req = new UserSearchReq();
        req.setName("wsen");
        req.setIsBlackUser(true);
        final PageVo<UserDto> query = userAdminService.query(req);

        System.out.println(JSON.toJSONString(query));
    }

}
