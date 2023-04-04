package com.tigerobo.x.pai.api.admin.req.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserSearchReq extends PageReqVo {


    @ApiModelProperty(allowEmptyValue = true)
    Integer id;
    String uuid;
    String account;
    String name;

    String wechatName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date startCreateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date endCreateTime;

    String mobile;
    String wechatUUid;
    @ApiModelProperty(value = "0-普通，1-管理员")
    Integer role;
    String accessSource;

    Boolean isBlackUser;

//    Boolean isDeleted;
}
