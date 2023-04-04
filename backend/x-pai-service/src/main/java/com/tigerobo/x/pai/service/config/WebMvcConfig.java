package com.tigerobo.x.pai.service.config;

//import com.tigerobo.x.pai.service.interceptor.AccessTokenVerifyInterceptor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Spring MVC 配置
 *
 * @author gavinL
 */
//@Configuration
//public class WebMvcConfig extends WebMvcConfigurationSupport {
//
//    @Bean
//    public AccessTokenVerifyInterceptor getAccessTokenVerifyInterceptor() {
//        return new AccessTokenVerifyInterceptor();
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(getAccessTokenVerifyInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns("/error/**");
//        super.addInterceptors(registry);
//    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
//
//}
