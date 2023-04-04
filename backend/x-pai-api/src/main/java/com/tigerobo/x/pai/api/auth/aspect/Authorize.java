package com.tigerobo.x.pai.api.auth.aspect;

import java.lang.annotation.*;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * 
 * @description: 用户授权信息注解
 * @modified By:
 * @version: $
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {

}
