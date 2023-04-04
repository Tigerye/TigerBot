package com.tigerobo.x.pai.dal.pay.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "api_agreement")
public class ApiAgreementPo {

    @Id
    Integer id;
    String name;
    String modelName;
    Integer skuId;
    Integer userId;
    String modelId;

    Integer startDay;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

    Boolean isDeleted;

    private Integer priceType;
    private BigDecimal price;
    String properties;
    String unit;

}
