package com.tigerobo.x.pai.api.dto.admin.ai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.ai.vo.AiArtImageVo;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ArtImageDto {


    Integer id;
    Long reqId;

    Integer userId;
    UserBriefVo user;

    String text;

    String inputImage;

    List<String> modifiers;

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


    List<AiArtImageVo.ProgressImage> progressImages;

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



    Boolean useFree;
    Integer coinTotal;

    /**
     * 0-不需要处理，1-已支付，2-已退还
     */
    Integer coinStatus;
}
