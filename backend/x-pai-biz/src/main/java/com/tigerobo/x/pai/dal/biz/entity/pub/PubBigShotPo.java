package com.tigerobo.x.pai.dal.biz.entity.pub;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "pub_big_shot")
public class PubBigShotPo {


    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    String name;
    private String logo;

    private Integer type;

    private String oriImg;
    private Integer srcId;

    String intro;
    private String alias;

    Boolean isDeleted;

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
