package com.tigerobo.x.pai.service.controller.web;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.auth.entity.*;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.auth.vo.AccountVo;
import com.tigerobo.x.pai.api.auth.vo.GroupVo;
import com.tigerobo.x.pai.api.biz.service.SummaryService;
import com.tigerobo.x.pai.api.dto.MemberDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.DemandVo;
import com.tigerobo.x.pai.api.vo.biz.ModelVo;
import com.tigerobo.x.pai.api.vo.biz.mine.MineCountVo;
import com.tigerobo.x.pai.api.config.ConditionConfig;
import com.tigerobo.x.pai.api.entity.Entity;
import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.vo.EmptyVo;
import com.tigerobo.x.pai.api.vo.WebRepVo;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.enums.OrgVerifyStatusEnum;
import com.tigerobo.x.pai.api.enums.PermissionTypeEnum;
import com.tigerobo.x.pai.api.vo.user.UserModelVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.auth.OrgInfoService;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.biz.member.MemberService;
import com.tigerobo.x.pai.biz.biz.service.WebGroupService;
import com.tigerobo.x.pai.biz.biz.service.WebModelService;
import com.tigerobo.x.pai.biz.biz.service.WebTaskService;
import com.tigerobo.x.pai.biz.biz.service.DemandServiceImpl;
import com.tigerobo.x.pai.biz.user.UcUserServiceImpl;
import com.tigerobo.x.pai.biz.user.UserModelService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

import static com.tigerobo.x.pai.api.exception.ResultCode.PAGE_NOT_EXIST;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 用户主页信息，其他用户查看也调用这边
 * @modified By:
 * @version: $
 */
@Slf4j
@RestController
@RequestMapping("/web/mine")
@Api(value = "主页信息", position = 2900, tags = "主页信息")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class XPaiMineController extends XPaiWebController {

    @Autowired
    private WebGroupService webGroupService;

    @Autowired
    private UserService userService;

    @Autowired
    private UcUserServiceImpl ucUserService;

    @Autowired
    private UserModelService userTaskService;

    @Autowired
    private WebModelService webModelService;

    @Autowired
    private SummaryService summaryService;
    @Autowired
    private DemandServiceImpl demandServiceV2;


    @Autowired
    private OrgInfoService orgInfoService;
    @Autowired
    private FollowService followService;
    @Autowired
    private MemberService memberService;

    @Autowired
    private RoleService roleService;
    @ApiOperation(value = "我的-登录信息", position = 10000)
    @PostMapping(path = {"loginInfo"}, consumes = "application/json", produces = "application/json")
    public Map<String,Object> mineLogin(@RequestBody EmptyVo requestVo) {

        Integer userId = ThreadLocalHolder.getUserId();
//        boolean login = userId != null && userId > 0;

        boolean login = userService.userIdExist(userId);

        Map<String,Object> data = new HashMap<>();
        data.put("login",login);

        return data;
    }

    @ApiOperation(value = "我的-统计信息", position = 10000)
    @PostMapping(path = {"mineCount"}, produces = "application/json")
    @Authorize
    public MineCountVo mineCount() {
        return summaryService.getMineCount();
    }

    @ApiOperation(value = "我的-基本信息", position = 10000)
    @PostMapping(path = {""}, consumes = "application/json", produces = "application/json")
    public AccountVo mine(@Valid @RequestBody WebRepVo requestVo) {
        log.info("mine:{}", JSON.toJSONString(requestVo));
        // TODO 账号获取
        Account account = otherHomeAccount(requestVo);
        if (account == null) {
            throw new APIException(PAGE_NOT_EXIST, "信息不存在", null);
        }

        // TODO 页面信息获取
        AccountVo accountVo = new AccountVo();
        Group group = null;
        User user = null;
        List<PermissionTypeEnum> permissionTypeEnumList = new ArrayList<>();

        OrgInfoDto org = new OrgInfoDto();
        org.setVerifyStatus(0);
        org.setFullName("");
        boolean owner = false;

        if (account.getType() == Entity.Type.USER) {
            Integer accountUserId = account.getId();
            OrgInfoDto orgInfo = orgInfoService.getOrgInfo(accountUserId);

            MemberDto member = memberService.getMember(accountUserId);
            accountVo.setMember(member);

            if (Role.OWNER == account.getRole()) {
                owner = true;
                user = userService.getById(accountUserId);

                if (user!=null&&user.getRoleType()!=null&&user.getRoleType().equals(1)){
                    permissionTypeEnumList.add(PermissionTypeEnum.VIEW_STATISTIC_BOARD);
                }

                if (orgInfo!=null){
                    org = orgInfo;
                }

            } else {
                user = userService.getUserByOther(accountUserId);
                if (orgInfo!=null&&OrgVerifyStatusEnum.VERIFIED.getStatus().equals(orgInfo.getVerifyStatus())){
                    org.setVerifyStatus(orgInfo.getVerifyStatus());
                    org.setFullName(orgInfo.getFullName());
                }

                boolean follow = followService.isFollow(user.getId(), FollowTypeEnum.USER.getType());
                accountVo.setFollow(follow);

            }

            accountVo.setUser(user);
            accountVo.setType(Entity.Type.USER);
            accountVo.setUuid(user.getUuid());
            accountVo.setAccount(user.getAccount());

            String currentGroupUuid = user.getCurrentGroupUuid();
            if (StringUtils.isNotBlank(currentGroupUuid)) {
                Group  groupInfo = webGroupService.getByUuidFromCache(currentGroupUuid);
                accountVo.setGroup(groupInfo);
            }else {
                Group userGroup = ucUserService.getUserGroup(accountUserId);
                accountVo.setGroup(userGroup);
            }

            accountVo.setRole(account.getRole());
        } else if (account.getType() == Entity.Type.GROUP) {

            group = webGroupService.getByUuidFromCache(account.getUuid());
            accountVo.setGroup(group);
            accountVo.setType(Entity.Type.GROUP);
            accountVo.setUuid(group.getUuid());
            accountVo.setAccount(group.getAccount());
            account.setRole(group.getRole());
            List<User> groupMemberList = userService.getGroupMemberList(group.getUuid());
            accountVo.setMemberList(groupMemberList);
            account.setRole(account.getRole());
        } else {
            throw new APIException(PAGE_NOT_EXIST, "页面不存在", null);
        }

        if (owner){
            accountVo.setRole(Role.OWNER);
        }else {
            accountVo.setRole(Role.GUEST);
        }

        accountVo.setPermissionList(permissionTypeEnumList);

        accountVo.setOrg(org);

        accountVo.setAdmin(roleService.isAdmin());
        // TODO 页面角色定义 && 用户认证
        return accountVo;
    }

    @ApiOperation(value = "用户-所属权限组", position = 10010)
    @PostMapping(path = {"/group-list"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public PageInfo<GroupVo> mineGroupList(@Valid @RequestBody WebRepVo requestVo) {
        log.info("mine/group-list:{}", JSON.toJSONString(requestVo));

        Integer userId = ThreadLocalHolder.getUserId();

        Group userGroup = ucUserService.getUserGroup(userId);
        PageInfo<GroupVo> pageInfo = new PageInfo<>();
        if (userGroup != null) {
            List<GroupVo> list = Arrays.asList(GroupVo.builder().group(userGroup).build());
            pageInfo.setList(list);
            pageInfo.setTotal(1);
        }
        return pageInfo;
    }

    @ApiOperation(value = "用户-项目列表", position = 10300)
    @PostMapping(path = {"/model-list"}, consumes = "application/json", produces = "application/json")
    public PageInfo<ModelVo> mineModelList(@Valid @RequestBody WebRepVo requestVo) {
        log.info("mine/model-list:{}", JSON.toJSONString(requestVo));
        // TODO 账号获取
        Account account = otherHomeAccount(requestVo);
        Integer userId = ThreadLocalHolder.getUserId();
        PageInfo<ModelVo> pageInfo = new PageInfo<>();

        User user = account.getUser();
        if (user == null) {
            return pageInfo;
        }
        if (user.getId() == null || !user.getId().equals(userId)) {
            return pageInfo;
        }
        return webModelService.getUserModels(userId);
    }

    @ApiOperation(value = "用户-任务列表", position = 10200)
    @PostMapping(path = {"/getUserModelTaskList"}, consumes = "application/json", produces = "application/json")
    public PageVo<UserModelVo> getUserModelTaskList(@Valid @RequestBody WebRepVo requestVo) {
        // TODO 账号获取
        log.info("mine/task-list:{}", JSON.toJSONString(requestVo));
        Account account = otherHomeAccount(requestVo);
        Integer userId = ThreadLocalHolder.getUserId();

        PageVo<UserModelVo> pageInfo = new PageVo<>();

        User user = account.getUser();

        if (user == null) {
            return pageInfo;
        }
        if (user.getId() == null || !user.getId().equals(userId)) {
            return pageInfo;
        }

        return userTaskService.getUserTaskList(userId);
//       return this.taskService.query(queryVo);
    }

    @ApiOperation(value = "用户-需求列表", position = 10100)
    @PostMapping(path = {"/demand-list"}, consumes = "application/json", produces = "application/json")
    public PageInfo<DemandVo> accountDemandList(@Valid @RequestBody WebRepVo requestVo) {
        log.info("mine/demand-list:{}", JSON.toJSONString(requestVo));
        // TODO 账号获取

        Account account = otherHomeAccount(requestVo);
        Integer userId = ThreadLocalHolder.getUserId();

        PageInfo<DemandVo> pageInfo = new PageInfo<>();

        if (account == null){
            return new PageInfo<>();
        }
        if (account.getType() == Entity.Type.USER){
            User user = account.getUser();

            if (user == null) {
                return pageInfo;
            }
            if (user.getId() == null || !user.getId().equals(userId)) {
                return pageInfo;
            }
            return this.demandServiceV2.getUserDemand(userId);
        }else if (account.getType() == Entity.Type.GROUP){
            return demandServiceV2.getGroupDemand(account.getId());
        }
        return pageInfo;

    }

}