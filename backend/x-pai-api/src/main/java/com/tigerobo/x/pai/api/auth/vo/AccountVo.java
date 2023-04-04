package com.tigerobo.x.pai.api.auth.vo;

import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.auth.entity.OrgInfoDto;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.dto.MemberDto;
import com.tigerobo.x.pai.api.entity.Entity;
import com.tigerobo.x.pai.api.enums.PermissionTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "授权模块-账户基本信息返回结果类")
public class AccountVo {

    @ApiModelProperty(value = "ID")
    protected Integer id;
    @ApiModelProperty(value = "UUID")
    protected String uuid;
    @ApiModelProperty(value = "类型")
    protected Entity.Type type;
    @ApiModelProperty(value = "页面角色")
    private Role role = Role.GUEST;


    @ApiModelProperty(value = "账户account")
    private String account;
    @ApiModelProperty(value = "用户信息：账户类型为USER时生效")
    private User user;
    @ApiModelProperty(value = "用户组信息")
    private Group group;

    MemberDto member;

    private boolean admin;

    @ApiModelProperty(value = "成员列表：账户类型为GROUP时生效")
    private List<User> memberList;
    boolean follow;

    OrgInfoDto org;

    List<PermissionTypeEnum> permissionList;

}
