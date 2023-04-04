package com.tigerobo.x.pai.service.config;


import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Slf4j
//@Component
//@Aspect
public class UserCachingOperationNameGenerator {
    private final Map<String, Integer> generated = Maps.newHashMap();

    @Around("execution(* springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator.startingWith(String))")
    public Object around(ProceedingJoinPoint point) {
        Object[] args = point.getArgs();
        return startingWith(String.valueOf(args[0]));
    }

    private String startingWith(String prefix) {
        if (generated.containsKey(prefix)) {
            generated.put(prefix, generated.get(prefix) + 1);
            String nextUniqueOperationName = String.format("%s_%s", prefix, generated.get(prefix));
            // log.warn("组件中存在相同的方法名称，自动生成组件方法唯一名称进行替换: {}", nextUniqueOperationName);
            return nextUniqueOperationName;
        } else {
            generated.put(prefix, 0);
            return prefix;
        }
    }
}


