package com.tigerobo.pai.admin;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.admin.req.ai.ArtImageAdminQueryReq;
import com.tigerobo.x.pai.api.admin.req.ai.ArtTextListReq;
import com.tigerobo.x.pai.api.dto.admin.ai.ArtImageDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.admin.ai.ArtImageAdminService;
import com.tigerobo.x.pai.biz.admin.cache.AdminHolder;
import com.tigerobo.x.pai.dal.admin.entity.SsoUserPo;
import org.apache.commons.lang3.time.DateUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class ArtImageAdminTest extends AdminBaseTest{

    @Autowired
    private ArtImageAdminService artImageAdminService;

    @Test
    public void queryTest(){

        ArtImageAdminQueryReq req = new ArtImageAdminQueryReq();
        PageVo<ArtImageDto> query = artImageAdminService.query(req);
        System.out.println(JSON.toJSONString(query));
    }

    @Test
    public void auditPassTest(){
        final SsoUserPo ssoUserPo = new SsoUserPo();
        ssoUserPo.setId(1);
        AdminHolder.setUser(ssoUserPo);
        artImageAdminService.auditPass(105827);
    }

    @Test
    public void refuseTest(){
        final SsoUserPo ssoUserPo = new SsoUserPo();
        ssoUserPo.setId(1);
        AdminHolder.setUser(ssoUserPo);
        artImageAdminService.auditRefuse(675,"不通过");

    }

    @Test
    public void downloadTest()throws Exception{


        final Date startTime = DateUtils.parseDate("2022-10-09", "yyyy-MM-dd");

        final Date endTime = DateUtils.parseDate("2022-10-10", "yyyy-MM-dd");
        ArtTextListReq req = new ArtTextListReq();
        req.setStartCreateTime(startTime);
        req.setEndCreateTime(endTime);

        final String textList = artImageAdminService.getTextList(req);

        System.out.println(textList);

    }
}
