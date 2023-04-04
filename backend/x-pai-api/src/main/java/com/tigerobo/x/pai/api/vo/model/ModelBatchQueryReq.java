package com.tigerobo.x.pai.api.vo.model;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ModelBatchQueryReq extends PageReqVo {
    String apiKey;
    @ApiModelProperty(value = "1-app调用,2-自主训练")
    Integer bizType;
}
