package com.tigerobo.x.pai.api.admin.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AdminOnlineStatusReq {
    Integer blogId;
    @ApiModelProperty("0-下线，1-上线")
    Integer onlineStatus;


}
