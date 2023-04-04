package com.tigerobo.x.pai.api.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PubSiteDto {

    Integer id;

    String name;

    String logoOss;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

    String intro;

    Integer isDeleted;

    Integer score;

    Integer vip;

    Integer articleCount;

}
