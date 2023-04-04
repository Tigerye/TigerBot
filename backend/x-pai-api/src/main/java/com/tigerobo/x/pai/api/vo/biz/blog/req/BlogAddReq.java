package com.tigerobo.x.pai.api.vo.biz.blog.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BlogAddReq {

    private Integer id;
    private String title;
    @ApiModelProperty(value = "摘要")
    private String summary;
    @ApiModelProperty(value = "内容")
    private String content;
    @ApiModelProperty(value = "头图")
    private String headImg;
    @ApiModelProperty(value = "0-草稿,1-上线")
    private Integer onlineStatus;
}
