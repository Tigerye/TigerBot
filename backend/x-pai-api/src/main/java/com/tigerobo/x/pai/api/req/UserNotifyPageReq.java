package com.tigerobo.x.pai.api.req;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserNotifyPageReq extends PageReqVo {
//    Integer userId;

    @ApiModelProperty(value = "通知大类型,1,订阅媒体")
    Integer notifyType;

}
