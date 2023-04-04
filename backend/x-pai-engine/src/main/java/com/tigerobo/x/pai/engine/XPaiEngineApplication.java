package com.tigerobo.x.pai.engine;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableApolloConfig(value = {"datasource-config","pai.config","pay","application"})
@SpringBootApplication(scanBasePackages = {"com.tigerobo.x.pai","com.algolet.pay"})
//@PropertySource(value = {"classpath:application.properties", "classpath:application-dal.properties"})
@EnableScheduling
public class XPaiEngineApplication {


    public static void main(String[] args) {

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "64");
        SpringApplication.run(XPaiEngineApplication.class, args);
    }
}
