package com.tigerobo.pai.biz.test.service.test.customer;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.biz.customer.AmlApiCustomerService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HupuModelServiceTest extends BaseTest {

    @Autowired
    private AmlApiCustomerService amlApiCustomerService;

    @Test
    public void transferAmlModelMapping(){

        amlApiCustomerService.checkHupu();
    }
}
