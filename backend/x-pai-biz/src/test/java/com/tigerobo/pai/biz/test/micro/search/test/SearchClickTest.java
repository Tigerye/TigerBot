package com.tigerobo.pai.biz.test.micro.search.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.search.api.dto.click.ClickDto;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.micro.search.IndexClickService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchClickTest extends BaseTest {
    @Autowired
    private IndexClickService indexClickService;

    @Test
    public void getTest(){

        IdUserReq req = new IdUserReq();
        req.setId(35418);
        final ClickDto appClickTotal = indexClickService.getAppClickTotal(req);

        System.out.println(JSON.toJSONString(appClickTotal));
    }
}
