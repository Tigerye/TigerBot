package com.tigerobo.x.pai.api.vo.biz.interact;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InteractVo {

    String bizId;
    Integer bizType;

    int viewNum;

    int shareNum;

    int commentNum;

    int thumbUpNum;

    boolean thumbUp;

    @ApiModelProperty(value = "用户是否评论")
    boolean userHasComment;

}
