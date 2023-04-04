package com.tigerobo.x.pai.api.vo.notify;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.dto.base.BaseMessageEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class NotifyVo extends INotifyVo{

    Integer id;

    Integer messageType;

    Integer notifyType;
    String notifyTypeName;

    String bizId;

    String title;

//    String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;


    Object messageEntity;

    List<NotifyJumpVo> jumpList;

}
