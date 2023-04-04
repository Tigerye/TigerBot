package com.tigerobo.pai.biz;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootApplication(scanBasePackages = {"com.tigerobo.x.pai","com.algolet.pay"})
@EnableApolloConfig(value = {"datasource-config","pai.config","pay"})
public class BizApp {
}
