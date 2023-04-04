package com.tigerobo.x.pai.api.vo.biz.req;

import com.tigerobo.x.pai.api.dto.DemandDataset;
import lombok.Data;

import java.util.List;

@Data
public class DemandDatasetReq{

    String demandUuid;

    List<DemandDataset> datasetList;

}
