package com.tigerobo.x.pai.api.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class OrgInfoDto {
    protected Integer id;
    private Integer userId;
    private String fullName;
    private String shortName;
    private String contactName;
    private String contactMobile;
    private Integer verifyStatus;

    private String verifyStatusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date verifyTime;

    String authUrl;

    String reason;
}
