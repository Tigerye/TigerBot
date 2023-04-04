package com.tigerobo.x.pai.api.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 内容返回基类
 * @modified By:
 * @version: $
 */
@Data
@SuperBuilder
@NoArgsConstructor
@ApiModel(value = "内容返回基类")
public abstract class ResponseVo implements Serializable {
}
