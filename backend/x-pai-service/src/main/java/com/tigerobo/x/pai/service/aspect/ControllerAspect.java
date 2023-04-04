package com.tigerobo.x.pai.service.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tigerobo.x.pai.api.auth.entity.Authorization;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.es.EsUserAccess;
import com.tigerobo.x.pai.api.vo.RequestVo;
import com.tigerobo.x.pai.biz.data.es.EsUserAccessService;
import com.tigerobo.x.pai.biz.user.TokenService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.http.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.List;
import java.util.Objects;

;

@Slf4j
@Aspect
@Component
public class ControllerAspect implements Ordered {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    @Autowired
    private EsUserAccessService esUserAccessService;
    @Pointcut("execution(public * com.tigerobo.x.pai.service.controller..*.*(..))")
    public void allLogic() {
    }

    @Before("allLogic()")
    public void doBeforeLogic(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        Integer userId = null;
        String ip = null;
        if (!isSkipToken(request)) {
            String token = getToken(request,joinPoint);
            userId = tokenService.getUserIdByToken(token);

            boolean exist = userService.userIdExist(userId);

            if (exist){
                ThreadLocalHolder.setUserId(userId);
            }else {
//                ThreadLocalHolder.setUserId(null);
                ThreadLocalHolder.clear();
            }
//            if (userId!=null&&userId>0){
//                log.error("");
//            }

            if (StringUtils.isNotBlank(token)){
                ThreadLocalHolder.setToken(token);
            }
        }
        ip = IpUtil.getIp(request);
        ThreadLocalHolder.setIp(ip);

        if (request!=null){
            String uri = request.getRequestURI();

            if(!ignoreUserAccess(uri)){
//                Integer userId = ThreadLocalHolder.getUserId();
//                String ip = ThreadLocalHolder.getIp();
                esUserAccessService.add(userId,ip);
            }
        }
    }

    private boolean ignoreUserAccess(String uri){
        if (uri.contains("heartbeat")||uri.contains("invoke")){
            return true;
        }

        return false;
    }

    private String getToken(HttpServletRequest request,JoinPoint joinPoint) {

        String inToken = request.getHeader("token");
        if (StringUtils.isNotBlank(inToken)){
            return inToken;
        }

        String paramToken = request.getParameter("token");
        if (StringUtils.isNotBlank(paramToken)){
            return paramToken;
        }

        String userInfo = request.getHeader("userInfo");
        Cookie[] cookies = request.getCookies();
        if (cookies!=null){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                if (name.equals("userInfo")){
                    userInfo = URLDecoder.decode(value);
                }
            }
        }
        String token = null;
        if (StringUtils.isNotBlank(userInfo)){
            try {
                JSONObject jsonObject = JSON.parseObject(userInfo);
                token = jsonObject.getString("token");
            }catch (Exception ex){
                log.error("intercept",ex);
            }
        }else {
            Object[] args = joinPoint.getArgs();
            if (args!=null){
                for (Object arg : args) {
                    if (arg instanceof RequestVo){
                        RequestVo req = (RequestVo)arg;
                        Authorization authorization = req.getAuthorization();
                        if (authorization!=null){
                            token = authorization.getToken();
                            break;
                        }
                    }
                }
            }
        }
        return token;
    }

    @AfterThrowing(throwing = "e", pointcut = "allLogic()")
    public void doAfterException(JoinPoint joinPoint, Throwable e) {
        ThreadLocalHolder.clear();
        ThreadLocalHolder.clearToken();
    }


    @After( "allLogic()")
    public void after(JoinPoint joinPoint) {
        ThreadLocalHolder.clear();
        ThreadLocalHolder.clearToken();
    }


    private boolean isSkipToken(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        if (requestURI==null||requestURI.contains("/heartbeat")){
            return true;
        }
        if (requestURI.startsWith("/web/uc/login")||requestURI.startsWith("/web/uc/register")){
            return true;
        }
        return false;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
