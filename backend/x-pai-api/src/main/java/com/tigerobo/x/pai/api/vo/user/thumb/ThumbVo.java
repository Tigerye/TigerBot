package com.tigerobo.x.pai.api.vo.user.thumb;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ThumbVo {
    Integer id;

    @ApiModelProperty(value = "点赞用户")
    UserBriefVo user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    String bizId;
    Integer bizType;

    String bizName;
}
