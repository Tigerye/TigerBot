package com.tigerobo.x.pai.dal.pay.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "api_balance_card")
@Data
public class ApiBalanceCardPo {


    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Integer userId;
    String modelId;
    Long totalNum;
    Long remainNum;

    Integer cardType;

    Integer status;
    Integer lastUpdateDay;
    Integer expireTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

    Boolean isDeleted;

}
