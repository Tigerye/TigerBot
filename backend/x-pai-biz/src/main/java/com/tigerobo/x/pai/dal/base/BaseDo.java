package com.tigerobo.x.pai.dal.base;

import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;
import tk.mybatis.mapper.annotation.ColumnType;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
//@SuperBuilder
//@AllArgsConstructor
//@NoArgsConstructor
public abstract class BaseDo extends ComDo{
//    @Column(name = "uuid", length = 32, unique = true)
    @ColumnType(jdbcType = JdbcType.VARCHAR, typeHandler = StringTypeHandler.class)
    protected String uuid;
}
