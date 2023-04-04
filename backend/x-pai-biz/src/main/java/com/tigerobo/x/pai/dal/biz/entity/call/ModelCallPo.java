package com.tigerobo.x.pai.dal.biz.entity.call;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "`model_call`")
public class ModelCallPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;

    Integer day;
    Integer userId;
    Integer modelId;
    Integer num;
}