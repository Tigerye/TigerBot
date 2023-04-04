package com.tigerobo.x.pai.api.admin.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BlogAdminQueryReq extends PageReqVo {

    Integer id;

    Integer vip;

    @ApiModelProperty(name = "来源0-algolet,1-site,2-bigshot",value = "区分来源")
    Integer sourceFrom;
    Integer siteId;
    Integer bigShotId;
    Integer userId;
    @ApiModelProperty(name = "爬虫id")
    Integer thirdId;
    Integer onlineStatus;

    int isDeleted;

    Integer categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date startPublishTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date endPublishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date startCreateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date endCreateTime;

    String keyword;



    Integer recommend;
    @ApiModelProperty(name = "1:黑科技")
    Integer tagType;

}
