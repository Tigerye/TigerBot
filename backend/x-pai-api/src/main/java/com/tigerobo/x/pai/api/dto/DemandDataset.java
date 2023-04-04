package com.tigerobo.x.pai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DemandDataset {

    private String demandUuid;
    private String filePath;
    private String name;
    private Date createTIme;
    private String createBy;
    private String updateBy;

}
