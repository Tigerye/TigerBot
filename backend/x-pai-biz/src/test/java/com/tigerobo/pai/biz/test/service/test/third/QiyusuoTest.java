package com.tigerobo.pai.biz.test.service.test.third;

import com.alibaba.fastjson.JSON;
import com.qiyuesuo.sdk.v2.bean.Contract;
import com.qiyuesuo.sdk.v2.response.CompanyAuthResult;
import com.qiyuesuo.sdk.v2.response.ContractPageResult;
import com.qiyuesuo.sdk.v2.response.SdkResponse;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.third.qiyuesuo.QysService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class QiyusuoTest extends BaseTest {


    @Autowired
    private QysService qysService;
    @Test
    public void pageUrlTest(){
        Long id = 2880346780938076574L;
        String mobile = "18301966691";

        SdkResponse<ContractPageResult> contractPageResultSdkResponse = qysService.pageUrl(id, mobile);
        System.out.println(contractPageResultSdkResponse.getResult().getPageUrl());
    }

    @Test
    public void viewCompanyTest(){
        String companyName = "信占轩";

        SdkResponse<CompanyAuthResult> view = qysService.vimEnterprise(companyName);
        String s = JSON.toJSONString(view);
        System.out.println(s);
    }
    @Test
    public void viewStatusTest(){

        String t = "2880355446865002616";
        Long id = Long.parseLong(t);
        SdkResponse<Contract> contractSdkResponse = qysService.viewContract(id);
//        if(responseObj.getCode() == 0) {
//            Contract contract = responseObj.getResult();
//            log.info("合同详情查询，合同主题：{}", contract.getSubject());
//        } else {
//            log.info("请求失败，错误码:{}，错误信息:{}", responseObj.getCode(), responseObj.getMessage());
//        }
        System.out.println(JSON.toJSONString(contractSdkResponse));
    }
}
