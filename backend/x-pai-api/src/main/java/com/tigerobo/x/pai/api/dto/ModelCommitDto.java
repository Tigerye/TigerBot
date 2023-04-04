package com.tigerobo.x.pai.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ModelCommitDto {

    Integer id;
    //    Integer userId;
    String modelId;

    String name;
    @ApiModelProperty(value = "提交信息")
    String memo;
    @ApiModelProperty(value = "文件地址")
    String filePath;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    Long size;
}
