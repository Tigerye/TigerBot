package com.tigerobo.pai.admin;

import com.tigerobo.x.pai.api.admin.req.UserCommitSiteAdminReq;
import com.tigerobo.x.pai.api.dto.UserCommitSiteDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.admin.UserCommitSiteAdminService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class UserCommitSiteTest extends AdminBaseTest{
    @Autowired
    private UserCommitSiteAdminService userCommitSiteAdminService;

    @Test
    public void queryTest() throws Exception {
        UserCommitSiteAdminReq req=new UserCommitSiteAdminReq();
//        req.setStatus(2);
//        req.setKeyword("西");
//        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//       Date date = sdf.parse("2021-12-15 15:42:08");
//        req.setEndUpdateTime(date);
        final PageVo<UserCommitSiteDto> pageList = userCommitSiteAdminService.getPageList(req);
        final List<UserCommitSiteDto> list = pageList.getList();
        list.stream().map(po->{
            System.out.println(po);
            return po;
        }).collect(Collectors.toList());

    }
}
