package com.tigerobo.x.pai.engine.task.aml.customer;

import com.tigerobo.x.pai.biz.biz.customer.AmlApiCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class AmlApiCustomerTask {

    @Autowired
    private AmlApiCustomerService amlApiCustomerService;


    @Scheduled(cron = "0 0 6-23/1 * * ?")
    public void run(){
        try {
            log.info("check hupu api id mapping");
            amlApiCustomerService.checkHupu();
        }catch (Exception ex){
            log.error("hupu",ex);
        }
    }
}
