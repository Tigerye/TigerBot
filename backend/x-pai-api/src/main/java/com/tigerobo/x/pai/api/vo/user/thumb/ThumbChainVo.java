package com.tigerobo.x.pai.api.vo.user.thumb;

import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.user.comment.CommentBizVo;
import com.tigerobo.x.pai.api.vo.user.comment.CommentGridVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ThumbChainVo {
    @ApiModelProperty(value = "点赞用户")
    ThumbVo thumb;

    CommentBizVo comment;
    @ApiModelProperty(value = "业务明细")
    IBusinessDetailVo businessDetail;

}
