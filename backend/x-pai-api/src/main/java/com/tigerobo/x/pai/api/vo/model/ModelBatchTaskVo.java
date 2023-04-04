package com.tigerobo.x.pai.api.vo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ModelBatchTaskVo {

    Integer id;
    Long reqId;
    Integer userId;
    String modelId;
    String modelName;
    String inputPath;
    String outPath;
    @ApiModelProperty(value = "0-待处理，1-处理成功，2-处理失败")
    Integer status;
    String statusName;
    Integer dealNum;
    Integer totalNum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    String errMsg;

    Object result;
    String fileName;

    String processText;

}
