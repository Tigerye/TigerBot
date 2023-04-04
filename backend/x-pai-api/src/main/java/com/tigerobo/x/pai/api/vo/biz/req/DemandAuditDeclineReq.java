package com.tigerobo.x.pai.api.vo.biz.req;

import com.tigerobo.x.pai.api.vo.UuidVo;
import lombok.Data;

@Data
public class DemandAuditDeclineReq extends UuidVo {
    String reason;
}
