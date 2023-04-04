package com.tigerobo.x.pai.service.controller.biz;

import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.config.ConditionConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Deprecated
@RestController
@RequestMapping("/public")
@Api(value = "通用结构说明",  tags = "业务模块-通用结构说明")
public class PublicController {

    @ApiOperation(value = "权限范围枚举")
    @PostMapping(path = "/scope-enum", consumes = "application/json", produces = "application/json")
    public Map<Integer,String> scopeEnum() {

        Map<Integer,String> data = new LinkedHashMap<>();

        data.put(Group.Scope.PERSONAL.getVal(), Group.Scope.PERSONAL.getName());
        data.put(Group.Scope.PUBLIC.getVal(), Group.Scope.PUBLIC.getName());
        return data;
    }
}
