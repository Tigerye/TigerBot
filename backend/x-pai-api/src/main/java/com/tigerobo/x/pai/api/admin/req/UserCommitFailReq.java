package com.tigerobo.x.pai.api.admin.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserCommitFailReq {
    Integer id;
    @ApiModelProperty(value = "失败信息")
    String msg;

}
