package com.tigerobo.x.pai.api.ai.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tigerobo.x.pai.api.ai.base.IAiUserInteract;
import com.tigerobo.x.pai.api.ai.req.AiArtImageGenerateReq;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.entity.BaseId;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.biz.interact.InteractVo;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AiArtImageVo implements BaseId, IAiUserInteract, IBusinessDetailVo {

    int bizType = BusinessEnum.ART_IMAGE.getType();
    String bizName = BusinessEnum.ART_IMAGE.toString();
    Integer id;
    Long reqId;


    List<AiArtImageGenerateReq.ArtImageParams> inputParam;

    Integer userId;
    User user;

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


    boolean follow;

    InteractVo interact;

    List<ProgressImage> progressImages;

    Integer imageProgress;

    Integer aiVersion;

    String styleType;

    Integer width;
    Integer height;


    Boolean useFree;
    Integer coinTotal;

    /**
     * 0-不需要处理，1-已支付，2-已退还
     */
    Integer coinStatus;


    Float imageStrength;

    Float promptWeight;
    Integer steps;

    @JsonProperty("nIter")
    Integer nIter;
    Integer seed;
    String modelVersion;

    List<String> iterImages;
    String gridImage;

    Boolean hide;
    @Builder
    @Data
    public static class ProgressImage{
        Integer progress;
        Integer rate;
        String imageUrl;
    }
}
