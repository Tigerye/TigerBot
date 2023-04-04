package com.tigerobo.x.pai.api.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BigShotDto {
    Integer id;
    String name;
    private String logo;

    private Integer type;

    private String oriImg;
    private Integer srcId;

    String intro;
    private String alias;

    Integer isDeleted;

    Integer vip;

    Integer subscribeStatus;
    Integer userId;
    String errMsg;
    String webUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;
}
