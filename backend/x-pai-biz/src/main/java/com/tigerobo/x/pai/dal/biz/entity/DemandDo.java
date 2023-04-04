package com.tigerobo.x.pai.dal.biz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.dal.base.BaseDo;
import lombok.Data;
    import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务系统-业务需求信息表
 * @modified By:
 * @version: $
 */
@Data
//@EqualsAndHashCode(callSuper = false)
//@SuperBuilder
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "`xpai-biz-demand`")
public class DemandDo extends BaseDo {


    protected String name;
    protected String intro;
    @Column(name = "`desc`")
    protected String desc;
    protected String nameEn;
    protected String introEn;
    protected String descEn;
    protected String image;

    private String budget;
    private Double budgetValue;
    private Date startDate;
    private Date deliveryDate;
    private Integer duration;
    private Integer phase;
    private Integer groupId;
    private String groupUuid;
    private Integer scope;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    private Date auditTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    private Date adoptTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    private Date modelOnlineTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    private Date testPassTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    private Date contractCompleteTime;



    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    private Date completeTime;

    Integer contractId;

    String contractCategoryId;

    String contractSampleUrl;

    String reason;

    String evaluation;

    String modelUuid;
}
