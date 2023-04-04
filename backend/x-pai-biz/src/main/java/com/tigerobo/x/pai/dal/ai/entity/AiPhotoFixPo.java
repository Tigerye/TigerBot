package com.tigerobo.x.pai.dal.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "ai_photo_fix")
@Data
public class AiPhotoFixPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Long reqId;
    Integer userId;
    String inputPhoto;
    String outputPhoto;
    Integer processStatus;
    Boolean isDeleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date reqTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date finishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    String apiKey;
    String msg;

    Integer appendColor;

    Integer width;
    Integer height;

    String compressOutputPhoto;
}
