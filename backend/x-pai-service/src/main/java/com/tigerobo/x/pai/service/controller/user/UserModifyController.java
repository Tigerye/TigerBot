package com.tigerobo.x.pai.service.controller.user;

import com.github.pagehelper.PageInfo;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.auth.entity.Authorization;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.auth.vo.GroupVo;
import com.tigerobo.x.pai.api.auth.vo.UserVo;
import com.tigerobo.x.pai.api.vo.QueryVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.WebRepVo;
import com.tigerobo.x.pai.biz.auth.process.GroupProcessor;
import com.tigerobo.x.pai.biz.auth.process.UserProcessor;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.biz.service.WebGroupService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.service.controller.web.XPaiWebController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-前端服务-权限接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/web/")
@Api(value = "用户信息修改", position = 2900, tags = "用户信息修改")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class UserModifyController extends XPaiWebController {
    @Autowired
    private UserService userService;
    @Autowired
    private GroupProcessor groupProcessor;
    @Autowired
    private UserProcessor userProcessor;

    @Autowired
    private OssService ossService;


    @Autowired
    private WebGroupService webGroupService;
    @ApiOperation(value = "用户-上传头像", position = 1001)
    @PostMapping(path = {"user/upload-avatar", "user-upload-avatar"})
    public String uploadAvatar(@NotNull @RequestParam("file") MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        byte[] data = file.getBytes();
        long timestamp = System.currentTimeMillis();
        String objectName = "auth/user/avatar/tmp/" + timestamp + "-" + fileName;
        return ossService.uploadFile(data,objectName);
    }

    @ApiOperation(value = "用户/用户组-账号校验", position = 1002)
    @PostMapping(path = {"user/check-account", "group/check-account"})
    public boolean checkAccount(@NotNull @RequestParam("account") String account) {
        if (StringUtils.isEmpty(account))
            throw new IllegalArgumentException("account");
        this.groupProcessor.checkAccount(Group.builder().account(account).build());
        this.userProcessor.checkAccount(User.builder().account(account).build());
        return true;
    }

    @ApiOperation(value = "用户/用户组-昵称校验", position = 1003)
    @PostMapping(path = {"user/check-name", "group/check-name"})
    public boolean checkName(@NotNull @RequestParam("name") String name) {
        if (StringUtils.isEmpty(name))
            throw new IllegalArgumentException("name");
        this.groupProcessor.checkName(Group.builder().name(name).build());
        this.userProcessor.checkName(User.builder().name(name).build());
        return true;
    }

    @ApiOperation(value = "用户-手机号校验", position = 1004)
    @PostMapping(path = {"user/check-mobile"})
    public boolean checkMobile(@NotNull @RequestParam("mobile") String mobile) {
        if (StringUtils.isEmpty(mobile))
            throw new IllegalArgumentException("mobile");
        this.userProcessor.checkMobile(User.builder().mobile(mobile).build());
        return true;
    }

    @ApiOperation(value = "用户-基本信息修改", position = 1101)
    @PostMapping(path = {"user/modify", "user-modify"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public UserVo userModify(@Valid @RequestBody WebRepVo requestVo) {
        User user = requestVo.getOrDefault("user", User.class, null);
        if (user == null){
            throw new IllegalArgumentException("参数为空");
        }

        // TODO 用户创建 并 登陆
        this.userService.update(user);
        return this.userService.getUserVo(user.getId());
    }


    @ApiOperation(value = "用户-密码修改", position = 1101)
    @PostMapping(path = {"user/pwd-modify", "user-pwd-modify"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO userPwdModify(@Valid @RequestBody WebRepVo requestVo) {
        // TODO 参数校验
        User user = requestVo.getOrDefault("user", User.class, null);
        if (user == null){
            throw new IllegalArgumentException("参数为空");
        }

        this.userProcessor.checkPassword(user);
        // TODO 修改密码 并 登陆
        this.userService.update(user);


//        return this.userService.login(userReqVo);
        return ResultVO.success();
    }

    @ApiOperation(value = "用户组-上传Logo", position = 1200)
    @PostMapping(path = {"group/upload-logo", "group-upload-logo"})
    public String uploadLogo(@NotNull @RequestParam("file") MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        byte[] data = file.getBytes();
        long timestamp = System.currentTimeMillis();
        String objectName = "auth/group/logo/tmp/" + timestamp + "-" + fileName;
        return ossService.uploadFile(data,objectName);
    }

    @Deprecated
    @ApiOperation(value = "用户组-列表", position = 1230)
    @PostMapping(path = {"group/list", "group-list"}, consumes = "application/json", produces = "application/json")
    public PageInfo<GroupVo> groupList(@Valid @RequestBody WebRepVo requestVo) {
        QueryVo queryVo = QueryVo.builder()
                .pageNum(requestVo.getPageNum())
                .pageSize(requestVo.getPageSize())
                .orderBy(requestVo.getOrderBy())
//                .authorization(requestVo.getAuthorization())
                .build();
        queryVo.set("type", "GROUP");
        queryVo.set("scope", "PUBLIC");
        queryVo.resetNotEmpty(requestVo.getParams());

        return new PageInfo<>();
//        return this.groupService.query(queryVo);
    }

    //1
    @ApiOperation(value = "用户-权限组加入", position = 1120)
    @PostMapping(path = {"user/group-join"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public Authorization userGroupJoin(@Valid @RequestBody WebRepVo requestVo) {

        // TODO 用户组授权你
        Group group = requestVo.getOrDefault("group", Group.class, null);
        Integer userId = ThreadLocalHolder.getUserId();
        return webGroupService.joinGroup(group,userId);
    }

}