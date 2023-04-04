package com.tigerobo.x.pai.service.controller.user;


import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.enums.ThumbAction;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.api.vo.user.thumb.ThumbChainVo;
import com.tigerobo.x.pai.api.vo.user.thumb.ThumbReq;
import com.tigerobo.x.pai.biz.user.thumb.ThumbOptService;
import com.tigerobo.x.pai.biz.user.thumb.UserThumbService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/thumb/")
@Api(value = "点赞", position = 101, tags = "点赞")
public class ThumbController {


    @Autowired
    private UserThumbService userThumbUpService;

    @Autowired
    private ThumbOptService thumbOptService;


    @ApiOperation(value = "我点赞的列表")
    @PostMapping(path = {"getMyThumbPage"}, consumes = "application/json", produces = "application/json")
//    @Authorize
    public PageVo<ThumbChainVo> getMyCommentList(@RequestBody UserPageReq req) {
        return userThumbUpService.getMyThumbPage(req);
    }

    @ApiOperation(value = "给我点赞列表")
    @PostMapping(path = {"getThumb2mePage"}, consumes = "application/json", produces = "application/json")
//    @Authorize
    public PageVo<ThumbChainVo> getCommentMeList(@RequestBody UserPageReq req) {
        return userThumbUpService.getThumb2mePage(req);
    }



    @ApiOperation(value = "点赞")
    @PostMapping(path = {"thumbUp"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO thumbUp(@RequestBody ThumbReq req) {

        thumbOptService.thumbUp(req);
        return ResultVO.success();

    }


    @ApiOperation(value = "取消点赞")
    @PostMapping(path = {"cancelThumbUp"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO cancelThumbUp(@RequestBody @NotNull ThumbReq req) {
        req.setActionType(ThumbAction.THUMB_UP.getType());
        thumbOptService.cancelThumbUp(req);
        return ResultVO.success();
    }

    @ApiOperation(value = "踩")
    @PostMapping(path = {"thumbDown"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO thumbDown(@RequestBody ThumbReq req) {
        thumbOptService.thumbDown(req);
        return ResultVO.success();
    }

    @ApiOperation(value = "取消踩")
    @PostMapping(path = {"cancelThumbDown"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO cancelThumbDown(@RequestBody @NotNull ThumbReq req) {

        thumbOptService.cancelThumbUp(req);
        return ResultVO.success();
    }

}
