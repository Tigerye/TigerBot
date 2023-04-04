package com.tigerobo.x.pai.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.dto.base.BaseMessageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserCommitSiteDto implements BaseMessageEntity {
    Integer id;
    Integer userId;
    @ApiModelProperty(value = "名称")
    String name;
    @ApiModelProperty(value = "网站地址")
    String url;
    @ApiModelProperty(value = "备注")
    String memo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

    /**
     * @see com.tigerobo.x.pai.api.enums.CommitSiteStatusEnum
     */
    @ApiModelProperty(value = "状态,0:待处理;1:处理中;2:拒绝;3:取消,5:处理成功;")
    Integer status;

    String statusName;
    String msg;

    Integer mediaType;
    Integer mediaId;
    Integer isDeleted;
    @ApiModelProperty(value = "用户名称")
    String userName;
}
