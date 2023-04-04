package com.tigerobo.x.pai.dal.aml.entity;

import com.tigerobo.x.pai.dal.base.BaseDo;
import com.tigerobo.x.pai.dal.base.handler.Boolean2TypeHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
//@EqualsAndHashCode(callSuper = false)
//@SuperBuilder
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "`aml_info`", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class AmlInfoDo extends BaseDo {
    Integer currentDataId;
    Integer baseModelId;
    Integer currentModelId;
    Byte status;
    String name;
    @ColumnType(jdbcType = JdbcType.TINYINT, typeHandler = Boolean2TypeHandler.class)
    Boolean hide;

    Integer trainType;
    Integer parentModelId;
    String modelPath;

    String msg;

}
