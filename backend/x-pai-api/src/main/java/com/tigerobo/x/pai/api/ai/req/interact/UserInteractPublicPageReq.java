package com.tigerobo.x.pai.api.ai.req.interact;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

@Data
public class UserInteractPublicPageReq extends PageReqVo {

    String bizId;
    Integer bizType;

    String tabType;
    String keyword;
    Long reqId;
}
