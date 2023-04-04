package com.tigerobo.x.pai.api.vo.user.comment;

import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommentChainVo {

    @ApiModelProperty(value = "评论")
    CommentGridVo comment;
    @ApiModelProperty(value = "评论的评论")
    CommentGridVo commentOn;
    @ApiModelProperty(value = "评论的业务内容")
    IBusinessDetailVo businessDetail;
}
