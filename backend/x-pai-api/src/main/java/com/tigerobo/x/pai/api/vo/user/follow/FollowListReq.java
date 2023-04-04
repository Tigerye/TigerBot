package com.tigerobo.x.pai.api.vo.user.follow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowListReq {
    Integer userId;
    @ApiModelProperty("业务类型0-关注平台用户,1-来源关注，2-bigShot")
    Integer bizType;
}
