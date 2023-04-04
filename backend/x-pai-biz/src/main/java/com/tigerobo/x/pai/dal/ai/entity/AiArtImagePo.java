package com.tigerobo.x.pai.dal.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "ai_art_image")
@Data
public class AiArtImagePo {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    Long reqId;

    Integer userId;
    String text;
    String inputImage;
    String modifiers;

    Integer status;
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

    Integer styleType;

    String progressImages;

    Integer imageProgress;

    String inputParam;

    Integer aiVersion;


    Integer thumbNum;

    String operator;

    Boolean useFree;
    Integer coinTotal;

    /**
     * 0-不需要处理，1-已支付，2-已退还
     */
    Integer coinStatus;

    Float imageStrength;
    Float promptWeight;
    Integer steps;

    Integer nIter;

    Integer seed;

    String iterImages;

    String gridImage;

    String modelVersion;

    Boolean hide;
}
