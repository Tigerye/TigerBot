package com.tigerobo.x.pai.api.auth.vo;

import com.google.common.collect.Lists;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.vo.ResponseVo;
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
 * @description: 授权模块-用户组信息返回结果类
 * @modified By:
 * @version: $
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "授权模块-用户组信息返回结果类")
public class GroupVo extends ResponseVo {
    @ApiModelProperty(value = "用户组信息")
    private Group group;
    @ApiModelProperty(value = "成员信息")
    private List<User> memberList = Lists.newArrayList();

    public void addMember(User user) {
        if (this.memberList == null)
            this.memberList = Lists.newArrayList();
        this.memberList.add(user);
    }
}
