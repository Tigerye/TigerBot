package com.tigerobo.x.pai.dal.aml.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.dal.base.BaseDo;
import lombok.Data;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.Date;

//@NoArgsConstructor
@Data
//@SuperBuilder
@Table(name = "`aml_model`", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class AmlModelDo extends BaseDo {

    Integer dataId;
    Integer baseModelId;
    Integer amlId;
    @Column(name = "`status`")
    Byte status;
    String name;

    @Column(name = "`precision`")
    BigDecimal precision;
    BigDecimal recall;
    BigDecimal avgPrecision;

    String workEnv;

    String resultPath;

    Integer epoch;
    Integer totalEpoch;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    Date modelFinishTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    Date modelReqTime;

    Integer serviceStatus;
    @Column(name = "`demo`")
    String demo;
    String style;

    String errMsg;

    String modelUrl;

    Integer parentModelId;


}
