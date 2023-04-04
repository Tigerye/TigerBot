package com.tigerobo.x.pai.api.dto.ad;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AdBannerDto {


    Integer id;

    String name;
    String enName;
    String image;

    String path;
    String icon;

    Integer sort;
    String bizId;
    Integer bizType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

    Boolean isDeleted;

    String slogan;
}
