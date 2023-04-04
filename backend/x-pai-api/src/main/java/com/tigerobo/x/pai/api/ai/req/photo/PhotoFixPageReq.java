package com.tigerobo.x.pai.api.ai.req.photo;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

@Data
public class PhotoFixPageReq extends PageReqVo {
    Long reqId;
    Integer userId;
}
