package com.tigerobo.x.pai.dal.biz.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Table;

@Data
@Table(name = "tag")
public class TagPo {
    private String uid;
    private String text;
    private String textEn;
    @ColumnType(column = "`desc`")
    private String desc;
    private String icon;
    private Integer type;
}
