package com.tigerobo.pai.biz.test.service.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.auth.entity.OrgInfoDto;
import com.tigerobo.x.pai.biz.auth.OrgInfoService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrgServiceTest extends BaseTest {

    @Autowired
    private OrgInfoService orgInfoService;

    @Test
    public void verifyTest(){

        OrgInfoDto orgInfoDto = getOrgInfoDto2();

        OrgInfoDto verify = orgInfoService.verify(orgInfoDto);
        System.out.println(JSON.toJSONString(verify));
    }

    @Test
    public void verifyAndUpdateTest(){

        orgInfoService.verifyFromViewQys(12);
    }

    private OrgInfoDto getOrgInfoDto() {
        OrgInfoDto orgInfoDto = new OrgInfoDto();
        ThreadLocalHolder.setUserId(1);

        orgInfoDto.setFullName("上海虎烨信息科技有限公司");
        orgInfoDto.setShortName("虎博");
        orgInfoDto.setContactMobile("18301966691");
        orgInfoDto.setContactName("王焕勇");
        return orgInfoDto;
    }

    private OrgInfoDto getOrgInfoDto2() {
        OrgInfoDto orgInfoDto = new OrgInfoDto();
        ThreadLocalHolder.setUserId(2);

        orgInfoDto.setFullName("上海虎烨测试公司");
        orgInfoDto.setShortName("虎烨测试");
        orgInfoDto.setContactMobile("18301966691");
        orgInfoDto.setContactName("王焕勇");
        return orgInfoDto;
    }
}
