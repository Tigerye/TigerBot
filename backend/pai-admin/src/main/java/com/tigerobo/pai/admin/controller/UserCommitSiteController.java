package com.tigerobo.pai.admin.controller;

import com.tigerobo.x.pai.api.admin.BlogAdminVo;
import com.tigerobo.x.pai.api.admin.auth.AdminAuthorize;
import com.tigerobo.x.pai.api.admin.req.BlogAdminQueryReq;
import com.tigerobo.x.pai.api.admin.req.UserCommitFailReq;
import com.tigerobo.x.pai.api.admin.req.UserCommitSiteAdminReq;
import com.tigerobo.x.pai.api.admin.req.UserCommitSuccessReq;
import com.tigerobo.x.pai.api.dto.UserCommitSiteDto;
import com.tigerobo.x.pai.api.req.UserCommitSiteReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.user.comment.UserCommentAddReq;
import com.tigerobo.x.pai.biz.admin.UserCommitSiteAdminService;
import com.tigerobo.x.pai.biz.user.UserCommitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/usercommitsite/")
@Api(value ="usercommitsite",tags = "usercommitsite")
public class UserCommitSiteController {
    @Autowired
    private UserCommitSiteAdminService userCommitSiteAdminService;
    @Autowired
    private UserCommitService userCommitService;

    @AdminAuthorize
    @ApiOperation(value = "usercommitsite-列表")
    @PostMapping(path = {"list"}, consumes = "application/json", produces = "application/json")
    public PageVo<UserCommitSiteDto> userComSiteList(@RequestBody UserCommitSiteAdminReq req) {

        return userCommitSiteAdminService.getPageList(req);
    }

    @AdminAuthorize
    @ApiOperation(value = "usercommitsite-提交申请")
    @PostMapping(path = {"add"}, consumes = "application/json", produces = "application/json")
    public ResultVO userComAdd(@Valid @RequestBody UserCommitSiteReq req) {
        userCommitSiteAdminService.userCommitAdd(req);
        return ResultVO.success();
    }

    @AdminAuthorize
    @ApiOperation(value = "usercommitsite-提交失败")
    @PostMapping(path = {"fail"}, consumes = "application/json", produces = "application/json")
    public ResultVO userComFail(@Valid @RequestBody UserCommitFailReq req) {
        userCommitSiteAdminService.userCommitFail(req);
        return ResultVO.success();
    }
    @AdminAuthorize
    @ApiOperation(value = "usercommitsite-提交成功")
    @PostMapping(path = {"success"}, consumes = "application/json", produces = "application/json")
    public ResultVO userComFail(@Valid @RequestBody UserCommitSuccessReq req) {
       userCommitSiteAdminService.userCommitSuccess(req);
        return ResultVO.success();
    }
}
