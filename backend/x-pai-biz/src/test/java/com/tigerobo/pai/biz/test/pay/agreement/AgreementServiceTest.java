package com.tigerobo.pai.biz.test.pay.agreement;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.pay.req.ApiAgreementReq;
import com.tigerobo.x.pai.biz.pay.api.ApiAgreementService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AgreementServiceTest extends BaseTest {

    @Autowired
    private ApiAgreementService apiAgreementService;

    @Test
    public void addAgreement(){


        ThreadLocalHolder.setUserId(3);
        ApiAgreementReq req = new ApiAgreementReq();
        req.setModelId("6466b9a3fc8efcddf81202b7ec38bee3");

        apiAgreementService.addAgreement(req);
    }
}
