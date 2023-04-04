package com.tigerobo.x.pai.api.admin.req.blog;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BlogTagReq {
    Integer id;
    @ApiModelProperty(value = "0-无标签，1-黑科技")
    Integer tagType;
}
