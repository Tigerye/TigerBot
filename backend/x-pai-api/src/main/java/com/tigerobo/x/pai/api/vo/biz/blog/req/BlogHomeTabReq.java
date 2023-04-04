package com.tigerobo.x.pai.api.vo.biz.blog.req;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BlogHomeTabReq extends PageReqVo {

    @ApiModelProperty(value = "recommend-今日份;blackTech-黑科技;bigshot-大咖说")
    private String tab;
}
