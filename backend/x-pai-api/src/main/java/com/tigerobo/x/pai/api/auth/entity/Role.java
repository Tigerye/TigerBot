package com.tigerobo.x.pai.api.auth.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public enum Role {
    OWNER(0, "所有者"),
    ADMIN(10, "管理员"),
    DEVELOPER(30,"成员"),

    GUEST(999, "游客");


    private final Integer val;
    private final String name;

    public static Role valueOf(int val) {

        for (Role value : values()) {
            if (value.getVal().equals(val)){
                return value;
            }
        }
        return GUEST;

    }
}
