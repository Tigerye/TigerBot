package com.tigerobo.x.pai.api.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tigerobo.x.pai.api.entity.BaseId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.StringUtils;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 权限模块-用户信息类
 * @modified By:
 * @version: $
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value={"currGroup", "group"})
@ApiModel(value = "权限模块-用户信息类")
public class User extends Account implements BaseId {
    @ApiModelProperty(value = "手机号")
    private String mobile;
    private String areaCode;
    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "微信")
    private String wechat;
    @ApiModelProperty(value = "电子邮箱")
    private String email;
    @ApiModelProperty(value = "密码")
    private String password;

    private Integer roleType;

    private String currentGroupUuid;
    private Integer currentGroupId;

    private String wechatName;
    @ApiModelProperty(value = "当前隶属权限组")
    private Group currGroup;

    boolean follow;

    String accessSource;
    @Override
    public void setType(Type type) {
        this.type = Type.USER;
    }

    @Override
    public Type getType() {
        this.type = Type.USER;
        return this.type;
    }

    public String getAvatar() {
        if (this.avatar == null)
            this.avatar = this.image;
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        if (!StringUtils.isEmpty(avatar)) {
            this.avatar = avatar;
            this.image = avatar;
        }
    }

    @Override
    public String getImage() {
        if (this.image == null)
            this.image = this.avatar;
        return this.image;
    }

    @Override
    public void setImage(String image) {
        if (!StringUtils.isEmpty(image)) {
            this.avatar = image;
            this.image = image;
        }
    }

    @Override
    public Group getGroup(){
        return this.toPersonalGroup();
    }

    public Group toPersonalGroup() {
        return Group.builder()
                .id(this.getId())
                .uuid(this.getUuid())
                .account(this.getAccount())
                .name(this.getName())
                .nameEn(this.getNameEn())
                .intro(this.getIntro())
                .introEn(this.getIntroEn())
                .desc(this.getDesc())
                .descEn(this.getDescEn())
                .image(this.getImage())
                .scope(Group.Scope.PERSONAL)
                .logo(this.getAvatar())
                .mobile(this.getMobile())
                .website(this.getWebsite())
                .owner(this)
                .build();
    }
}
