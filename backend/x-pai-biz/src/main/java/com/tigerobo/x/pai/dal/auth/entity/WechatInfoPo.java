package com.tigerobo.x.pai.dal.auth.entity;

import lombok.Data;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Table(name = "wechat_info", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class WechatInfoPo {

    private Integer id;
    private String unionId;
    private String content;
//    @ColumnType(jdbcType = JdbcType.TINYINT, typeHandler = Boolean2TypeHandler.class)
//    private Boolean isDeleted;
}
