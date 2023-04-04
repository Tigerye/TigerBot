package com.tigerobo.x.pai.dal.basket.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "basket_correct_word")
@Data
public class CorrectWordPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;

    String originTitle;
    String correctTitle;
    @Column(name = "`key`")
    String key;
    String ip;
}
