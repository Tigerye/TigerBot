package com.tigerobo.x.pai.dal.biz.entity.call;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "`model_source_call`")
public class ModelSourceCallPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;

    Integer day;
    Integer userId;
    String modelId;

    Integer source;
    Integer type;

    Integer num;

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    protected Date updateTime;

}
