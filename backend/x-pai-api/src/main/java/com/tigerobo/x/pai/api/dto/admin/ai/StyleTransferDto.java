package com.tigerobo.x.pai.api.dto.admin.ai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.ai.vo.AiStyleTransferVo;
import com.tigerobo.x.pai.api.auth.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StyleTransferDto {


    Integer id;
    Long reqId;

    Integer userId;
    User user;

    String contentImage;

    Integer status;

    Integer processStatus;
    String processStatusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date publishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date dealTime;

    Integer totalProgress;
    Integer progressRate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    String msg;

    String outputImage;

    String role;

    String title;
    String desc;


    List<AiStyleTransferVo.ProgressImage> progressImages;

    Integer imageProgress;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date reqTime;

    Integer progress;

    Integer isDeleted;

    String workPath;

//    String apiKey;

    Integer retryTime;
    Integer width;
    Integer height;

}
