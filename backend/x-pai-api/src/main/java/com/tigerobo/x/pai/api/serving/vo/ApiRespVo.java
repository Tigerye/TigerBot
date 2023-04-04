package com.tigerobo.x.pai.api.serving.vo;

import com.tigerobo.x.pai.api.vo.ResponseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 服务模块-服务接口返回结果类
 * @modified By:
 * @version: $
 */
@Data
public class ApiRespVo{
    @ApiModelProperty(value = "返回结果")
    private Object result;
    Long reqId;
}