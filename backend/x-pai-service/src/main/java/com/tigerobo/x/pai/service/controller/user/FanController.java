package com.tigerobo.x.pai.service.controller.user;

import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.user.UserIdPageReq;
import com.tigerobo.x.pai.api.vo.user.fan.FanVo;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.user.fans.FanService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fan/")
@Api(value = "粉丝")
public class FanController {

    @Autowired
    private FanService fanService;


    @ApiOperation(value = "用户粉丝数")
    @PostMapping(path = {"getUserFanCount"}, produces = "application/json")
    public Map<String,Object> getMyFanCount(@RequestBody UserIdReq req) {
        final Integer userId = req.getUserId();
        Map<String,Object> data = new HashMap<>();
        final int i = fanService.countUserFans(userId);
        data.put("count",i);
        return data;
    }

    @ApiOperation(value = "我的粉丝数")
    @PostMapping(path = {"getMyFanCount"}, produces = "application/json")
    public Map<String,Object> getFanCount() {
        final Integer userId = ThreadLocalHolder.getUserId();
        Map<String,Object> data = new HashMap<>();
        final int i = fanService.countUserFans(userId);
        data.put("count",i);
        return data;
    }

    @ApiOperation(value = "我的粉丝列表")
    @PostMapping(path = {"getMyFanPage"}, produces = "application/json")
    public PageVo<FollowVo> getMyFollowList(@RequestBody UserIdPageReq req) {

        req.setUserId(ThreadLocalHolder.getUserId());
        return fanService.getMyFansPage(req);
    }


    @ApiOperation(value = "用户粉丝列表")
    @PostMapping(path = {"getUserFanPage"}, produces = "application/json")
    public PageVo<FollowVo> getUserFollowList(@RequestBody UserIdPageReq req) {

        return fanService.getUserFansPage(req);
    }

    @Data
    public static class UserIdReq{

        Integer userId;
    }
}
