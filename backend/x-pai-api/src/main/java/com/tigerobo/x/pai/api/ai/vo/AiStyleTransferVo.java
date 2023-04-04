package com.tigerobo.x.pai.api.ai.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.ai.base.IAiUserInteract;
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
public class AiStyleTransferVo implements BaseId, IAiUserInteract, IBusinessDetailVo {

    int bizType = BusinessEnum.STYLE_TRANSFER.getType();
    String bizName = BusinessEnum.STYLE_TRANSFER.toString();
    Integer id;
    Long reqId;

    Integer userId;
    User user;

    String contentImage;

    String styleImage;

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


    @Builder
    @Data
    public static class ProgressImage{
        Integer progress;
        Integer rate;
        String imageUrl;
    }
}
