package com.tigerobo.x.pai.api.admin.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BigShotAdminReq extends PageReqVo {
    Integer id;
    String keyword;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date startCreateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date endCreateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date startUpdateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date endUpdateTime;
    @ApiModelProperty(value = "不传-所有，0-普通，1-订阅vip")
    Integer vip;
    @ApiModelProperty(value = "不传-所有，0-未删除，1-已删除")
    Integer isDeleted;
}
