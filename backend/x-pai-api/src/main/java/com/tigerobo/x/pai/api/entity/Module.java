package com.tigerobo.x.pai.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Getter
@AllArgsConstructor
public enum Module {
    UNKNOWN(0, "unknown", "未知"),
    AUTH(10, "auth", "用户认证"),
    BIZ(20, "biz", "业务系统"),
    SERVING(30, "serving", "统一服务"),
    REPO(40, "repo", "模型仓库"),
    ENGINE(50, "engine", "计算引擎");

    private final Integer val;
    private final String name;
    private final String desc;

    public static Module valueOf(int val) {
        switch (val) {
            case 10:
                return AUTH;
            case 20:
                return BIZ;
            case 30:
                return SERVING;
            case 40:
                return REPO;
            case 50:
                return ENGINE;
            default:
                return UNKNOWN;
        }
    }

    public static Module valueOf2(String name) {
        return valueOf(name.toUpperCase());
    }
}