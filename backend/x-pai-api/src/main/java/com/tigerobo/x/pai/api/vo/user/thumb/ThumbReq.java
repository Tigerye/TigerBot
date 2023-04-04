package com.tigerobo.x.pai.api.vo.user.thumb;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ThumbReq {
    private String bizId;
    @ApiModelProperty(value = "业务类型:4-blog,5评论")
    private Integer bizType;
    @ApiModelProperty(value = "点击类型:0-点赞,1-踩")
    private Integer actionType;
}
