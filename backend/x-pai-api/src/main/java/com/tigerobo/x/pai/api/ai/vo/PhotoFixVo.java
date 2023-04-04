package com.tigerobo.x.pai.api.ai.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.biz.interact.InteractVo;
import lombok.Data;

import java.util.Date;

@Data
public class PhotoFixVo implements IBusinessDetailVo {
    int bizType = BusinessEnum.PHOTO_FIX.getType();
    Integer id;
    Long reqId;

    Integer userId;
    User user;
    String inputPhoto;
    Integer processStatus;
    String processStatusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    String msg;

    String outputPhoto;

    String role;

    InteractVo interact;

    String predictRemainTime;

    Integer width;
    Integer height;

    boolean appendColor;
    String compressOutputPhoto;
}
