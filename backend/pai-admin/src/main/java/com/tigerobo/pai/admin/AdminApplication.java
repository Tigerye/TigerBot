package com.tigerobo.pai.admin;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
@SpringBootApplication(scanBasePackages = {"com.tigerobo.x.pai","com.tigerobo.pai","com.algolet.pay"})
@EnableApolloConfig(value = {"datasource-config","pai.config","pay"})
@EnableScheduling
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class AdminApplication {

    public static void main(String[] args) {

        SpringApplication.run(AdminApplication.class, args);
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "64");
    }
}
