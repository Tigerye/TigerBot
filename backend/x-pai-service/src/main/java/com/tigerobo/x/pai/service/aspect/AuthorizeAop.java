package com.tigerobo.x.pai.service.aspect;

import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 用户授权信息注解AOP
 * @modified By:
 * @version: $
 */
@Slf4j
@Aspect
@Component
public class AuthorizeAop {

    @Before("@annotation(com.tigerobo.x.pai.api.auth.aspect.Authorize)")
    public void authorize(JoinPoint joinPoint) throws Throwable {
//        log.info("Authorize");
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null) {
            Object target = joinPoint.getTarget();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = target.getClass().getMethod(signature.getName(), signature.getParameterTypes());
            log.error("authorize-err:{},{}",joinPoint.getClass(),method.getName());
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
//        check(joinPoint);
    }

}
