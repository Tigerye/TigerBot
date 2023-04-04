package com.tigerobo.x.pai.service.aspect;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
//@Aspect
//@Component
//@Order(-2)
@Deprecated
public class LogRecordAspect {


    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)"
            + " || @annotation(org.springframework.web.bind.annotation.GetMapping)"
            +"|| @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void allLogic(){

    }
    @Around("allLogic()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.currentTimeMillis();
        String url = null, method = null, uri = null, iP = null, requestParam = null;
        try {
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
            url = request.getRequestURL().toString();
            method = request.getMethod();
            uri = request.getRequestURI();
//            iP = getIp(request);
//            ThreadLocalHolder.setIp(iP);
            requestParam = JSON.toJSONString(Arrays.stream(pjp.getArgs())
                    .filter(param -> !(param instanceof HttpServletRequest)
                            && !(param instanceof HttpServletResponse)
                            && !(param instanceof MultipartFile)
                            && !(param instanceof MultipartFile[])
                    ).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("REQUEST: failed to parse request!",e);
            throw e;
        }

        // result的值就是被拦截方法的返回值
        Object result = pjp.proceed();
        long end = System.currentTimeMillis();

        if (uri!=null&&!uri.contains("heartbeat")&&!uri.endsWith("invoke")
        &&!uri.contains("uploadCorrectWord")){
            log.info("REQUEST: cost->{} ms, uri->{}, IP->{}, method->{}, url->{}, params->{}", end - begin, uri, iP, method, url, requestParam);
        }
        ThreadLocalHolder.clearIp();
        return result;
    }

    /**
     * 获取目标主机的ip
     *
     * @param request
     * @return
     */
    private final static List<String> IP_HEAD_LIST = Lists.newArrayList("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "X-Real-IP");

    private static String getIp(HttpServletRequest request) {
        for (String ipHead : IP_HEAD_LIST) {
            String ip = request.getHeader(ipHead);
            if (!StringUtils.isEmpty(ip) && "unknown".equalsIgnoreCase(ip))
                return request.getHeader(ipHead).split(",")[0];
        }
        return "0:0:0:0:0:0:0:1".equals(request.getRemoteAddr()) ? "127.0.0.1" : request.getRemoteAddr();
    }

    /**
     * 判断是否含有特殊字符
     *
     * @param text
     * @return true为包含，false为不包含
     */
    private final static Pattern INVALID_PATTERN = Pattern.compile("(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(frame|<frame|iframe|<iframe|img|<img|javascript|<javascript|script|<script|alert|select|update|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)");

    private static boolean isSpecialChar(String text) {
        // 过滤请求参数中的非法字符，防止XSS攻击、SQL盲注等
//        Matcher m = INVALID_PATTERN.matcher(text);
//        return m.find();
        return false;
    }
}