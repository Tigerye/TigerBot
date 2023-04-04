package com.tigerobo.x.pai.api.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tigerobo.x.pai.api.dto.MemberDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 授权模块-授权信息类
 * @modified By:
 * @version: $
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "授权模块-授权信息类")
public class Authorization {
    @ApiModelProperty(value = "登录TOKEN")
    private String token;
    @ApiModelProperty(value = "用户ID")
    private String uid;
    @ApiModelProperty(value = "用户组ID")
    private String gid;
    @ApiModelProperty(value = "用户组信息")
    private Group group;

    @ApiModelProperty(value = "用户信息")
    private User user;
    @ApiModelProperty(value = "会员信息")
    private MemberDto member;

    @ApiModelProperty(value = "用户组角色")
    private Role role = Role.GUEST;
    @ApiModelProperty(value = "是否合法")
    @Builder.Default
    @JsonIgnore
    private boolean valid = false;
    @ApiModelProperty(value = "校验码")
    @Builder.Default
    @JsonIgnore
    private Integer checkCode = -1;
    @ApiModelProperty(value = "补充信息")
    private Map<String, Object> extras;

    public void setGroup(Group group) {
        this.group = group;
        if (group != null)
            this.gid = group.getUuid();
        else
            this.gid = null;
    }

    /**
     * 是否合法
     *
     * @return true/false
     */
    public boolean isValid() {
        return this.valid;
    }

}
