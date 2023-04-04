package com.tigerobo.x.pai.api.vo.biz.blog.req;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserPageReq extends PageReqVo {
    private String keyword;
    private Integer recommend;
}
