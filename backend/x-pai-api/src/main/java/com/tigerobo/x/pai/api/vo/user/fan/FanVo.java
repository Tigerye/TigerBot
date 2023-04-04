package com.tigerobo.x.pai.api.vo.user.fan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class FanVo {
    Integer id;

    @ApiModelProperty(value = "粉丝用户")
    UserBriefVo fanUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

}
