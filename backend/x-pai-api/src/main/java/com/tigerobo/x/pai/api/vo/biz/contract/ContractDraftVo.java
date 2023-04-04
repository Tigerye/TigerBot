package com.tigerobo.x.pai.api.vo.biz.contract;

import lombok.Data;

@Data
public class ContractDraftVo {
    private String contractPageUrl;

    private String demandUuid;
    private Integer contractId;

    private Integer contractChannel;
    private String contractUrl;
}
