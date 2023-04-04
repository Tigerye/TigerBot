package com.tigerobo.x.pai.api.admin.req.ai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ArtImageAdminQueryReq extends PageReqVo {

    Integer id;
    Long reqId;
    String text;
    String title;
    Integer userId;

    @ApiModelProperty(value = "0,-下线;1-已上线")
    Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date startCreateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date endCreateTime;
    @ApiModelProperty(value = "0:排队中;1:待处理;2:处理失败;3:待审核;5:已成功;")
    Integer processStatus;
    Integer isDeleted;

}
