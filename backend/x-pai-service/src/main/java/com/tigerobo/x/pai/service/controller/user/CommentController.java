package com.tigerobo.x.pai.service.controller.user;


import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.api.vo.user.comment.CommentQueryReq;
import com.tigerobo.x.pai.api.vo.user.comment.UserCommentAddReq;
import com.tigerobo.x.pai.biz.user.comment.BusinessCommentService;
import com.tigerobo.x.pai.biz.user.comment.CommentOptService;
import com.tigerobo.x.pai.biz.user.comment.UserCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/comment/")
@Api(value = "评论列表", position = 2900, tags = "评论列表")
public class CommentController {


    @Autowired
    private BusinessCommentService businessCommentService;

    @Autowired
    private CommentOptService commentOptService;

    @Autowired
    private UserCommentService userCommentService;

//
//    @ApiOperation(value = "我的评论列表")
//    @PostMapping(path = {"getMineCommentList"}, consumes = "application/json", produces = "application/json")
//    @Authorize
//    public PageVo getMineCommentList(@RequestBody CommentQueryReq req) {
//        return commentService.getBizCommentList(req);
//    }
//
    @ApiOperation(value = "我的评论列表")
    @PostMapping(path = {"getMyCommentList"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public PageVo getMyCommentList(@RequestBody UserPageReq req) {
        return userCommentService.getMyCommentList(req);
    }

    @ApiOperation(value = "评论我的列表")
    @PostMapping(path = {"getCommentMeList"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public PageVo getCommentMeList(@RequestBody UserPageReq req) {
        return userCommentService.getCommentMeList(req);
    }


    @ApiOperation(value = "业务id下的评论列表")
    @PostMapping(path = {"getCommentList"}, consumes = "application/json", produces = "application/json")
    public PageVo getCommentList(@RequestBody CommentQueryReq req) {
        return businessCommentService.getBizCommentList(req);
    }


    @ApiOperation(value = "添加评论")
    @PostMapping(path = {"addComment"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO addComment(@RequestBody UserCommentAddReq req) {

        commentOptService.add(req);
        return ResultVO.success();

    }


    @ApiOperation(value = "删除评论")
    @PostMapping(path = {"deleteComment"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO deleteComment(@RequestBody @NotNull IdReqVo req) {
        commentOptService.delete(req.getId());
        return ResultVO.success();

    }



}
