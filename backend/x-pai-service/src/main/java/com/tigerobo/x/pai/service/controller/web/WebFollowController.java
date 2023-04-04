package com.tigerobo.x.pai.service.controller.web;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.user.follow.FollowListReq;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/web/follow/")
@Api(value = "关注接口", tags = "关注接口")
public class WebFollowController {

    @Autowired
    private FollowService followService;


    @ApiOperation(value = "关注", position = 2130)
    @PostMapping(path = {"add"}, produces = "application/json")
    @Authorize
    public ResultVO follow(@RequestBody @NotNull FollowReq req) {

        Integer userId = ThreadLocalHolder.getUserId();

        Integer bizId = req.getBizId();
        Preconditions.checkArgument(bizId != null, "业务id为空");
        Integer bizType = req.getBizType();
        Preconditions.checkArgument(bizType != null, "业务类型为空");

        FollowTypeEnum typeEnum = FollowTypeEnum.getByType(bizType);
        Preconditions.checkArgument(typeEnum != null, "当前业务不支持");
        followService.follow(userId, bizId, bizType);
        return ResultVO.success();
    }

    @ApiOperation(value = "取消关注", position = 2130)
    @PostMapping(path = {"cancel"}, produces = "application/json")
    @Authorize
    public ResultVO cancelFollow(@RequestBody @NotNull FollowReq req) {

        return doCancelFollow(req);
    }

    private ResultVO doCancelFollow(FollowReq req) {
        Integer userId = ThreadLocalHolder.getUserId();

        Integer bizId = req.getBizId();
        Preconditions.checkArgument(bizId != null, "业务id为空");
        Integer bizType = req.getBizType();
        Preconditions.checkArgument(bizType != null, "业务类型为空");

        FollowTypeEnum typeEnum = FollowTypeEnum.getByType(bizType);
        Preconditions.checkArgument(typeEnum != null, "当前业务不支持");
        followService.cancelFollow(userId, bizId, bizType);
        return ResultVO.success();
    }

    @ApiOperation(value = "用户关注列表", position = 2130)
    @PostMapping(path = {"getUserFollowList"}, produces = "application/json")
    public List<FollowVo> getUserFollowList(@RequestBody @NotNull FollowListReq req) {
        if (req.getBizType() == null) {
            return followService.getFollowList(req.getUserId(), Arrays.asList(0, 1, 2));
        }
        List<FollowVo> followList = followService.getFollowList(req.getUserId(), req.getBizType());
        return followList;
    }

    @ApiOperation(value = "关注数")
    @PostMapping(path = {"getFollowNum"}, produces = "application/json")
    public Map<String,Object> getFollowNum(@RequestBody FollowListReq req) {

        Map<String,Object> data = new HashMap<>();
        final int i = followService.getFollowCountData(req.getUserId(), req.getBizType());
        data.put("count",i);
        return data;
    }

    @Data
    private static class FollowReq {


        Integer bizId;
        @ApiModelProperty("业务类型0-关注平台用户,1-来源关注，，2-bigShot")
        Integer bizType;
    }


}
