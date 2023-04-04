package com.tigerobo.x.pai.service.aspect;

import com.tigerobo.x.pai.api.vo.RequestVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/5 11:34 AM
 * @description: 全局处理增强版Controller，前置处理请求体
 * @modified By:
 * @version: $
 */
@Deprecated
//@Slf4j
//@RestControllerAdvice(basePackages = {"com.tigerobo.x.pai.service.controller"}) // 注意哦，这里要加上需要扫描的包
public class RequestControllerAdvice implements RequestBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return httpInputMessage;
    }

    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        // 打印参数
//        log.debug("{}#{} request: {}", methodParameter.getContainingClass().getSimpleName(), Objects.requireNonNull(methodParameter.getMethod()).getName(), JSON.toJSONString(o));
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }
}
