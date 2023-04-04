package com.tigerobo.x.pai.api.vo.biz.pub;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BigShotQueryReq extends PageReqVo {
    private String keyword;
    @ApiModelProperty(value = "不传-所有，0-普通，1-订阅vip")
    private Integer vip;
}
