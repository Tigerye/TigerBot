package com.tigerobo.x.pai.dal.ai.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "ai_param_dict")
public class AiParamDictPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    String name;
    Integer type;
    Boolean isDeleted;

    String imgUrl;
    String text;
    String classType;
}
