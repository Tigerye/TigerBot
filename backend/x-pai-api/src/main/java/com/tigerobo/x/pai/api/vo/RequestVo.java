package com.tigerobo.x.pai.api.vo;

import com.tigerobo.x.pai.api.auth.entity.Authorization;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

//import javax.validation.constraints.Max;
//import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 内容请求基类
 * @modified By:
 * @version: $
 */
@Data
@SuperBuilder
@NoArgsConstructor
//@AllArgsConstructor
@ApiModel(value = "内容请求基类")
public abstract class RequestVo implements Serializable {
//    @Max(value = 1000, message = "PageNum must be less then or equal to 1000")
//    @Min(value = 1, message = "PageNum must be large then or equal to 1")
    @Builder.Default
    @ApiModelProperty(value = "PageNum")
    protected Integer pageNum = 1;
//    @Max(value = 500, message = "PageSize must be less then or equal to 500")
//    @Min(value = 1, message = "PageSize must be large then or equal to 1")
    @Builder.Default
    @ApiModelProperty(value = "PageSize")
    protected Integer pageSize = 200;
    @Builder.Default
    @ApiModelProperty(value = "排序方式")
    protected String orderBy = "id desc";
//    @Builder.Default
//    @ApiModelProperty(value = "用户授权信息", position = 3)
    protected Authorization authorization = null;

}
