package com.tigerobo.x.pai.api.vo.user.comment;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

@Data
public class CommentQueryReq extends PageReqVo {

    String bizId;
    Integer bizType;

}
