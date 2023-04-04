package com.tigerobo.x.pai.api.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tigerobo.x.pai.api.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.util.StringUtils;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "权限模块-用户组信息类")
public class Group extends Account {
    @ApiModelProperty(value = "范围")
    @Builder.Default
    private Scope scope = Scope.UNKNOWN;
    @ApiModelProperty(value = "LOGO")
    private String logo;
    @ApiModelProperty(value = "联系电话，或者Owner电话")
    private String mobile;
    @ApiModelProperty(value = "所有者ID（默认创建者）")
    @JsonIgnoreProperties(value = {"createTime", "updateTime", "website", "mobile", "email", "image", "nameEn", "intro", "introEn", "desc", "descEn"})
    private User owner;
    private Integer ownerId;

    @Override
    public void setType(Type type) {
        this.type = Type.GROUP;
    }

    @Override
    public Type getType() {
        this.type = Type.GROUP;
        return this.type;
    }

    public String getLogo() {
        if (this.logo == null)
            this.logo = this.image;
        return this.logo;
    }

    public void setLogo(String logo) {
        if (!StringUtils.isEmpty(logo)) {
            this.logo = logo;
            this.image = logo;
        }
    }

    @Override
    public String getImage() {
        if (this.image == null)
            this.image = this.logo;
        return this.image;
    }

    @Override
    public void setImage(String image) {
        if (!StringUtils.isEmpty(image)) {
            this.logo = image;
            this.image = image;
        }
    }

    @Override
    public Group getGroup() {
        return this;
    }

    @Getter
    @AllArgsConstructor
    public enum Scope {
        UNKNOWN(0, "未知"),
        PERSONAL(10, "个人"),
        PRIVATE(20, "私有"),
        PUBLIC(30, "公开");

        private final Integer val;
        private final String name;

        public static Group.Scope valueOf(int val) {
            switch (val) {
                case 10:
                    return PERSONAL;
                case 20:
                    return PRIVATE;
                case 30:
                    return PUBLIC;
                default:
                    return UNKNOWN;
            }
        }
    }
}
