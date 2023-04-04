package com.tigerobo.x.pai.dal.biz.entity.model;

import lombok.Data;
import tk.mybatis.mapper.annotation.ColumnType;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "model_category")
public class ModelCategoryPo {
    @Id
    protected Integer id;
    private String uid;
    private String text;
    @ColumnType(column = "`desc`")
    private String desc;
    private String icon;
    Boolean isDeleted;

    Integer type;
}
