package com.tigerobo.x.pai.service.controller.web;

import com.algolet.pay.biz.service.ActivityService;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.biz.ActivityMemberService;
import com.tigerobo.x.pai.biz.user.BlackUserService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/activity/")
@Api(value = "activity",  tags = "activity活动")
public class ActivityController {


    @Autowired
    private ActivityMemberService activityMemberService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private BlackUserService blackUserService;
    @ApiOperation(value = "领取会员")
    @PostMapping(path = "adoptMember", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO adoptMember() {
        activityMemberService.adoptMember();
        return ResultVO.success();
    }
    @ApiOperation(value = "签到")
    @PostMapping(path = "signIn", produces = "application/json")
    @Authorize
    public ResultVO signIn() {
        final Integer userId = ThreadLocalHolder.getUserId();
        final boolean blackUser = blackUserService.isBlackUser(userId);
        if (blackUser){
            return ResultVO.fail("用户没权限");
        }
        activityService.signIn(userId);
        return ResultVO.success();
    }

    @ApiOperation(value = "是否签到")
    @PostMapping(path = "hasSignIn", produces = "application/json")
    @Authorize
    public Map<String,Object> hasSignIn() {
        final Integer userId = ThreadLocalHolder.getUserId();
        final boolean hasSign = activityService.hasSign(userId);
        Map<String,Object> data = new HashMap<>();
        data.put("hasSignIn",hasSign);
        return data;
    }

}
