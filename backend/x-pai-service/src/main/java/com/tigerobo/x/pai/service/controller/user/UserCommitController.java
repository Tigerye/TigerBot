package com.tigerobo.x.pai.service.controller.user;


import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.req.UserCommitPageReq;
import com.tigerobo.x.pai.api.req.UserCommitSiteReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.user.UserCommitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/commit/")
@Api(value = "用户提交",tags = "用户提交")
public class UserCommitController {


    @Autowired
    private UserCommitService userCommitService;

    @ApiOperation(value = "用户提交网站订阅申请")
    @PostMapping(path = {"site"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO commitSite(@RequestBody UserCommitSiteReq req) {
        userCommitService.addCommit(req);
        return ResultVO.success();
    }

    @ApiOperation(value = "提交分页列表")
    @PostMapping(path = {"getCommitPage"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public PageVo getCommitPage(@RequestBody UserCommitPageReq req) {
        return userCommitService.getUserCommitPage(req);
    }

}
