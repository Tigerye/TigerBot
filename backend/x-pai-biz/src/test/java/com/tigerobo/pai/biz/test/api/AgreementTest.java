package com.tigerobo.pai.biz.test.api;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.pay.req.ApiAgreementReq;
import com.tigerobo.x.pai.biz.pay.api.ApiAgreementService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AgreementTest extends BaseTest {

    @Autowired
    private ApiAgreementService apiAgreementService;


    @Test
    public void addTest(){

        String modelId = "bf78e192c11b8e138694149fb1bdeee9";
        ApiAgreementReq req = new ApiAgreementReq();
        req.setModelId(modelId);
        req.setSkuId(4);

        ThreadLocalHolder.setUserId(3);
        apiAgreementService.addAgreement(req);

    }
}
