package com.tigerobo.x.pai.dal.biz.entity.ad;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "`ad_banner`")
public class AdBannerPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;

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
