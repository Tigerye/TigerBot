package com.tigerobo.x.pai.api.ai.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.enums.ArtImageType;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ArtImagePublicPageReq extends PageReqVo {

    @ApiModelProperty(value = "new,hot,follow")
    String tabType;
    @ApiModelProperty(value = "oneHour,oneDay,oneWeek,all")
    String hotType;
    @ApiModelProperty("风格modifier")
    String modifier;
    String keyword;


    Long reqId;
    @ApiModelProperty("处理状态:0-排队中，1-处理中,2-已成功,5-处理失败")
    Integer processStatus;
    @ApiModelProperty("发布状态:0-未发布，1-已发布")
    Integer status;
    Integer userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date startCreateTime;
    @ApiModelProperty(hidden = true)
    List<Integer> userIds;

    String styleType;

    @ApiModelProperty(hidden = true)
    Integer styleTypeId;

    public Integer getStyleTypeId(){
        final ArtImageType imageType = ArtImageType.getByName(styleType);
        return imageType == null?null:imageType.getType();
    }
}
