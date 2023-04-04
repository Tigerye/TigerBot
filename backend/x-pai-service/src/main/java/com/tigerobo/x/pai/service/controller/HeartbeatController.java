package com.tigerobo.x.pai.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 服务心跳接口
 * @modified By:
 * @version: $
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/")
@Api(value = "服务心跳接口", position = 0, tags = "服务心跳接口", hidden = true)
public class HeartbeatController {
    @ApiOperation(value = "心跳", position = 0)
    @GetMapping(path = "/heartbeat")
    public Boolean heartbeat() {
        return true;
    }
}
