package com.tigerobo.x.pai.service;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@SpringBootApplication(scanBasePackages = {"com.tigerobo.x.pai","com.algolet.pay"})
//@PropertySource(value = {"classpath:application.properties"})
@EnableApolloConfig(value = {"application","datasource-config","pai.config","pay"})
//@EnableScheduling
@EnableTransactionManagement
public class XPaiServiceApplication {
    public static void main(String[] args) {
//        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "64");
        SpringApplication.run(XPaiServiceApplication.class, args);
    }
}
