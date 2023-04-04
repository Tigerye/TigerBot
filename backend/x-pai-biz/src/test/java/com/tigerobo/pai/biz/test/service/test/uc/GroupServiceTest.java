package com.tigerobo.pai.biz.test.service.test.uc;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.biz.biz.service.WebGroupService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GroupServiceTest  extends BaseTest {

    @Autowired
    private WebGroupService webGroupService;

    @Test
    public void test(){
        String uuid = "d8c8c4fc53d4256bd1395d73eb7378b1";
        Group group = webGroupService.getByUuidFromCache(uuid);

        System.out.println(JSON.toJSONString(group));

        group = webGroupService.getByUuidFromCache(uuid);

        System.out.println(JSON.toJSONString(group));

    }
}
