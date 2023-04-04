package com.tigerobo.x.pai.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class DemandSuggest {


    private String demandUuid;
    private String title;
    private String docUrl;
    private String operator;
    private String suggestDesc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createTime;

//    Authorization authorization;
}
