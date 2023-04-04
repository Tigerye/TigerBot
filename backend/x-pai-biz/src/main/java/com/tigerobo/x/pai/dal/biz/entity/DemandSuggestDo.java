package com.tigerobo.x.pai.dal.biz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.dal.base.handler.Boolean2TypeHandler;
import lombok.Data;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;
import tk.mybatis.mapper.annotation.ColumnType;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "`demand_suggest`")
public class DemandSuggestDo {
    @Id
    @KeySql(useGeneratedKeys = true)
    protected Integer id;
    @Column(name = "create_time", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    protected Date createTime;
    @Column(name = "create_by", length = 64)
    @ColumnType(jdbcType = JdbcType.VARCHAR, typeHandler = StringTypeHandler.class)
    protected String createBy;
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    protected Date updateTime;
    @Column(name = "update_by", length = 64)
    @ColumnType(jdbcType = JdbcType.VARCHAR, typeHandler = StringTypeHandler.class)
    protected String updateBy;
    @Column(name = "is_deleted")
    @ColumnType(jdbcType = JdbcType.TINYINT, typeHandler = Boolean2TypeHandler.class)
    protected Boolean isDeleted;

    private String demandUuid;
    private String title;
    private String docUrl;
    private String operator;
    private String suggestDesc;

}
