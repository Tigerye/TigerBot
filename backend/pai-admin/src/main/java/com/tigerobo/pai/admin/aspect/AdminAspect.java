package com.tigerobo.pai.admin.aspect;

import com.tigerobo.x.pai.biz.admin.SsoUserService;
import com.tigerobo.x.pai.biz.admin.cache.AdminHolder;
import com.tigerobo.x.pai.biz.data.es.EsUserAccessService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.admin.entity.SsoUserPo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

;

@Slf4j
@Aspect
@Component
public class AdminAspect implements Ordered {

    @Autowired
    private SsoUserService ssoUserService;
    @Pointcut("execution(public * com.tigerobo.pai.admin.controller..*.*(..))")
    public void allLogic() {
    }

    @Before("allLogic()")
    public void doBeforeLogic(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        if (!isSkipToken(request)) {
            String token = getToken(request,joinPoint);
            SsoUserPo userPo = ssoUserService.getByToken(token);

            AdminHolder.setUser(userPo);
            ThreadLocalHolder.setToken(token);
        }
//        String uri = request.getRequestURI();
    }


    private String getToken(HttpServletRequest request,JoinPoint joinPoint) {

        return request.getHeader("token");
    }

    @After("allLogic()")
    public void doAfter() {
        ThreadLocalHolder.clear();
        ThreadLocalHolder.clearToken();
    }

    @AfterThrowing(throwing = "e", pointcut = "allLogic()")
    public void doAfterException(JoinPoint joinPoint, Throwable e) {
        ThreadLocalHolder.clear();
        ThreadLocalHolder.clearToken();
    }


    private boolean isSkipToken(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        if (requestURI==null||requestURI.contains("/heartbeat")){
            return true;
        }
        if (requestURI.startsWith("/api/getTotal")){
            return true;
        }
        return false;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
