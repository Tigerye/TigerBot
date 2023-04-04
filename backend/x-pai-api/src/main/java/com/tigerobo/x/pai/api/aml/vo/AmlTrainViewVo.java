package com.tigerobo.x.pai.api.aml.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.aml.dto.AmlInfoDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AmlTrainViewVo {

    private AmlInfoDto amlInfo;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    Date modelCreateTime;
    //模型信息
    Integer totalItems;
    BigDecimal precision;
    BigDecimal recall;

    BigDecimal avgPrecision;
}
