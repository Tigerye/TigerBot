package com.tigerobo.x.pai.dal.basket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "basket_model_call")
@Data
public class BasketModelCallPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;

    String token;
    Integer groupId;

    String apiKey;

    String apiName;
    Integer callNum;

    Integer processStatus;
    String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date startCallTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date endCallTime;

    String memo;

    //type =1 总量
    Integer type;


}
