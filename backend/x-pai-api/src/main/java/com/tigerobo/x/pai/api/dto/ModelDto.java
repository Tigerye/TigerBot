package com.tigerobo.x.pai.api.dto;

import lombok.Data;

@Data
public class ModelDto {

    private Integer limited;

    
    private String repoAddr;
    private String apiUri;
    private String style;
    private Integer status;
    private Integer subject;
    private Integer groupId;
    private String groupUuid;
}
