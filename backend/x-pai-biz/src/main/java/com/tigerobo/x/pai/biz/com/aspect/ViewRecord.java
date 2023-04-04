package com.tigerobo.x.pai.biz.com.aspect;

import java.lang.annotation.*;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 页面访问计数器
 * @modified By:
 * @version: $
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewRecord {
}
