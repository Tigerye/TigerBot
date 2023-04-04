package com.tigerobo.x.pai.dal.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "ai_style_transfer")
@Data
public class AiStyleTransferPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    Long reqId;

    Integer userId;

    Integer status;
    String contentImage;
    String styleImage;
    Integer styleImageId;

    Integer processStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date reqTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date dealTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date publishTime;
    Integer progress;

    Integer totalProgress;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    Boolean isDeleted;

    String workPath;

    String msg;

    String outputImage;

    String title;
    @Column(name = "`desc`")
    String desc;
    String apiKey;

    Integer retryTime;
    Integer width;
    Integer height;

    String progressImages;

    Integer imageProgress;
}
