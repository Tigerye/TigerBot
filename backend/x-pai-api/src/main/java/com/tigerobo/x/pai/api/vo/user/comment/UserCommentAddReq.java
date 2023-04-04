package com.tigerobo.x.pai.api.vo.user.comment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserCommentAddReq {
    private String bizId;
    @ApiModelProperty(value = "业务类型:4-blog")
    private Integer bizType;

    private String content;
    Integer replyCommentId;
}
