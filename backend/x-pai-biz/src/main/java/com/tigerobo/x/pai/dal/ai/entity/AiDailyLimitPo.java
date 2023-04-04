package com.tigerobo.x.pai.dal.ai.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
@Data
@Table(name = "ai_daily_limit")
public class AiDailyLimitPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;

    private Integer userId;


    Integer num;

    Boolean isDeleted;
}
