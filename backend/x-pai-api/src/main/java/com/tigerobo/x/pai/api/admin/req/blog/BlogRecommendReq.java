package com.tigerobo.x.pai.api.admin.req.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BlogRecommendReq {

    Integer id;
    @ApiModelProperty(value = "0-不推荐，1-推荐")
    Integer recommend;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date recommendTime;
}
