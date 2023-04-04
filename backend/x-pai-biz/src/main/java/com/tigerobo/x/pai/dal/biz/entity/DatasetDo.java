package com.tigerobo.x.pai.dal.biz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.dal.base.handler.Boolean2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;
import tk.mybatis.mapper.annotation.ColumnType;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务系统-数据集信息表
 * @modified By:
 * @version: $
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`xpai-biz-dataset`", uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "uuid"})})
public class DatasetDo {


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

    @ColumnType(jdbcType = JdbcType.VARCHAR, typeHandler = StringTypeHandler.class)
    protected String uuid;

    protected String name;
    protected String intro;
    @Column(name = "`desc`")
    protected String desc;
    protected String nameEn;
    protected String introEn;
    protected String descEn;
    protected String image;

    private String format;
    private Integer limited;
    private String filePath;
    private Integer groupId;
    private String groupUuid;
}
