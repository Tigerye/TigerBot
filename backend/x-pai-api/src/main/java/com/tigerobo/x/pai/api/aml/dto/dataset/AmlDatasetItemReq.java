package com.tigerobo.x.pai.api.aml.dto.dataset;

import com.tigerobo.x.pai.api.vo.RequestVo;
import lombok.Data;

@Data
public class AmlDatasetItemReq extends RequestVo {

    private Integer id;
    private String statisticTypeEnum;
    private String searchKey;
}
