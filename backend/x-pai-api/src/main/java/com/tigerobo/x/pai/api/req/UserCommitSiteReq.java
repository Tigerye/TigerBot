package com.tigerobo.x.pai.api.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserCommitSiteReq {

    @ApiModelProperty(value = "名称")
    String name;
    @ApiModelProperty(value = "网站地址")
    String url;
    @ApiModelProperty(value = "备注")
    String memo;

}
