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
public class AiSpatioActionVo implements BaseId, IAiUserInteract, IBusinessDetailVo {

    int bizType = BusinessEnum.SPATIO_ACTION.getType();
    String bizName = BusinessEnum.SPATIO_ACTION.toString();
    Integer id;
    Long reqId;

    Integer userId;
    User user;
    String inputVideo;

    Integer status;

    Integer processStatus;
    String processStatusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date publishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date dealTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    String msg;

    String output;

    String role;

    String title;
    String desc;
    boolean follow;

    InteractVo interact;

}