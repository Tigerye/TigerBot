package com.tigerobo.x.pai.api.admin.req;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

import java.util.Date;

@Data
public class UserCommitSiteAdminReq extends PageReqVo {
    Integer id;
    Integer userId;
    String keyword;
    Integer status;
    Integer mediaType;
    Integer mediaId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date startCreateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date endCreateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date startUpdateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date endUpdateTime;
    Integer isDeleted;
}
