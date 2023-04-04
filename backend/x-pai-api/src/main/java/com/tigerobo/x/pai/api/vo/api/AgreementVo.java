package com.tigerobo.x.pai.api.vo.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;

@Data
public class AgreementVo {

    Integer id;
    String name;
    Integer skuId;
    Integer userId;
    String modelId;

    Integer startDay;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    private Integer priceType;
    private BigDecimal price;
    LinkedHashMap<String,Object> properties;
    String unit;

    String taskId;
    String appShortName;
}
