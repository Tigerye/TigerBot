package com.tigerobo.x.pai.engine.config;

import com.dianping.cat.servlet.CatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CatFilterConfig {

    @Bean
    public FilterRegistrationBean catFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CatFilter filter = new CatFilter();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("cat-filter");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
