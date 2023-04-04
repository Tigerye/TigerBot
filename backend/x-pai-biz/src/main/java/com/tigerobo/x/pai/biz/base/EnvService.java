package com.tigerobo.x.pai.biz.base;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@Service
public class EnvService {

    @Resource
    Environment environment;

    @Getter
    @Value("${pai.env.aml.prefix}")
    private String envPrefix;

    @Value("${pai.web.domain}")
    private String webDomainUrl;

    @Value("${pai.search.api.uuid:}")
    private String searchApiUuid;

    public String getAppId(){
        return environment.getProperty("app.id");
    }

    public boolean isProd(){
//        log.info("test:{}",envPrefix);
        return "p".equalsIgnoreCase(envPrefix);
    }

    public String getWebDomain(){
        return webDomainUrl;
    }


    public String getSearchApiUuid(){
        return searchApiUuid;
    }
}
