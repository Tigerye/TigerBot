package com.tigerobo.pai.admin.aspect;

import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.biz.admin.cache.AdminHolder;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.admin.entity.SsoUserPo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class AdminAuthorizeAop {

    @Before("@annotation(com.tigerobo.x.pai.api.admin.auth.AdminAuthorize)")
    public void authorize(JoinPoint joinPoint) throws Throwable {
//        log.info("Authorize");
        SsoUserPo user = AdminHolder.getUser();
        if (user == null) {
            Object target = joinPoint.getTarget();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = target.getClass().getMethod(signature.getName(), signature.getParameterTypes());
            log.error("authorize-err:{},{}",joinPoint.getClass(),method.getName());
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
//        check(joinPoint);
    }

}
