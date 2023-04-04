package com.tigerobo.x.pai.dal.biz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "`model_call_log`")
public class ModelCallLogPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    Date createTime;

    Integer day;
    Integer userId;
    String modelId;
    String content;
    String ip;
    String appId;
    Integer source;
    String result;
}
