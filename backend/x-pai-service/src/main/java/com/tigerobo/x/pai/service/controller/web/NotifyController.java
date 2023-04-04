package com.tigerobo.x.pai.service.controller.web;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.req.UserNotifyPageReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.notify.NotifyCountVo;
import com.tigerobo.x.pai.api.vo.notify.NotifyVo;
import com.tigerobo.x.pai.biz.biz.NotifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/notify/")
@Api(value = "notify",  tags = "通知")
public class NotifyController {



    @Autowired
    private NotifyService notifyService;

    @ApiOperation(value = "获取通知列表")
    @PostMapping(path = "getNotifyPage", consumes = "application/json", produces = "application/json")
    @Authorize
    public PageVo<NotifyVo> getNotifyPage(@RequestBody UserNotifyPageReq  req) {
        return notifyService.getNotifyPage(req);
    }


    @ApiOperation(value = "获取通知未读数")
    @PostMapping(path = "countNotifyUnRead", consumes = "application/json", produces = "application/json")
//    @Authorize
    public Map<String,Object> countNotifyUnRead() {
        int i = notifyService.countUnRead();

        Map<String,Object> data = new LinkedHashMap<>();

        data.put("unReadCount",i);

        return data;
    }

    @ApiOperation(value = "获取各类通知未读数")
    @PostMapping(path = "countNotifyUnReadGroupByType", consumes = "application/json", produces = "application/json")
    @Authorize
    public Map<String,Object> countNotifyUnReadGroupByType() {
        List<NotifyCountVo> list = notifyService.countUnReadGroupByType();
        Map<String,Object> data = new LinkedHashMap<>();
        data.put("unReadCount",list);
        return data;
    }


}
