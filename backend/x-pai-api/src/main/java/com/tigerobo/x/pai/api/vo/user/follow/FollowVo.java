package com.tigerobo.x.pai.api.vo.user.follow;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * todo 设计bigshot,site,user 复用 2021-12-13
 */
@Data
public class FollowVo {

    Integer id;
    @ApiModelProperty(value = "业务类型,0-关注平台用户,1-来源关注，2-bigShot其他平台人物")
    Integer bizType;

    String logoOss;
    String name;
    String alias;

    Integer vip = 0;

    String intro;
    boolean follow;
    String role;

    String userAccount;

    String platformName;

    @Deprecated
    String logo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
}
