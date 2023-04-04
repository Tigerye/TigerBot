package com.tigerobo.x.pai.api.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tigerobo.x.pai.api.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 权限模块-账号信息类
 * @modified By:
 * @version: $
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value={"group"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "权限模块-账号信息类")
public class Account extends Entity {
    @ApiModelProperty(value = "账号名，仅包括字母、数字、短线、下划线")
    protected String account;
    @ApiModelProperty(value = "个人主页")
    protected String website;
    protected Role role = Role.GUEST;
    User user;

}
