package com.tigerobo.x.pai.dal.biz.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`model_new_word`")
public class ModelNewWordPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    protected Integer id;

    private Integer day;

    String type;
    String words;
}
