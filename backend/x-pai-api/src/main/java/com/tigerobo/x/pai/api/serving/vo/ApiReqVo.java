package com.tigerobo.x.pai.api.serving.vo;

import com.tigerobo.x.pai.api.vo.QueryVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 服务模块-服务接口请求类
 * @modified By:
 * @version: $
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "服务模块-服务接口请求类")
public class ApiReqVo extends QueryVo {
    @ApiModelProperty(value = "服务Key")
    private String apiKey;

    private String appId;
    private String accessToken;

    private Integer source;
    Integer userId;

    @ApiModelProperty(value = "1-应用模型，2-自主训练模型")
    Integer bizType;

    private Boolean isDemo;

    private Integer searchIndexId;
}
