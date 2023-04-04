package com.tigerobo.x.pai.api.serving.vo;

import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.biz.entity.Model;
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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "服务模块-API接口信息返回结果类")
public class ApiVo extends ResponseVo {
    @ApiModelProperty(value = "API接口信息")
    private API api;
//    @ApiModelProperty(value = "关联模型信息")
//    private Model model;
}
