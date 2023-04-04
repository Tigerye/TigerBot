package com.tigerobo.x.pai.api.es;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
public class EsModelCall {

    Long id;
    Integer day;
    Integer userId;
    String modelId;

    /**
     * 1-应用api，2-自主训练
     */
    Integer type;

    String content;
    String ip;
    String appId;
    /**
     * 调用来源类型
     */
    Integer source;
    String result;

    Integer callNum;
    boolean demo;
    Long dealTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    String bizId;
}
