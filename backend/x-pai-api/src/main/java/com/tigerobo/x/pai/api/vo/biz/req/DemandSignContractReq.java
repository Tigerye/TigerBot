package com.tigerobo.x.pai.api.vo.biz.req;

import com.tigerobo.x.pai.api.vo.UuidVo;
import lombok.Data;

@Data
public class DemandSignContractReq extends UuidVo {

//    private Integer contractChannel;
    private String contractUrl;

}
