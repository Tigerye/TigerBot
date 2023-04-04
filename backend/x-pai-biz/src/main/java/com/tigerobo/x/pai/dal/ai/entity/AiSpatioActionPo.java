package com.tigerobo.x.pai.dal.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "ai_spatio_action")
@Data
public class AiSpatioActionPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    Long reqId;

    Integer userId;

    Integer status;
    String inputVideo;

    Integer processStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date reqTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date dealTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date publishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    Boolean isDeleted;

    String workPath;

    String msg;

    String output;

    String title;
    @Column(name = "`desc`")
    String desc;
    String apiKey;

    Integer retryTime;
}
