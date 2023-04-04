package com.tigerobo.x.pai.dal.biz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.dal.base.handler.Boolean2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`contract_info`")
public class ContractInfoPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    protected Integer id;
    @Column(name = "create_time", insertable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    protected Date createTime;
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    protected Date updateTime;
    @Column(name = "is_deleted")
    @ColumnType(jdbcType = JdbcType.TINYINT, typeHandler = Boolean2TypeHandler.class)
    protected Boolean isDeleted;

    private String contractName;
    private Integer userId;
    private Integer orgId;
    private String orgFullName;
    private String orgContactName;
    private String orgContactMobile;
    private String bizId;
    private String bizName;
    private String subject;

    Integer status;
    String thirdContractId;
    String productId;
    String productName;
    String contractUrl;
    String sponsorName;
    String sponsorMobile;
    String sponsorContactName;

    String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    protected Date sponsorTime;

    String categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    private Date completeTime;

    private Integer channel;

}
