package com.tigerobo.x.pai.dal.biz.entity;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "demand_log")
public class DemandLogPo {
    private Integer id;
    Integer demandId;
    Integer type;
    String content;
    Integer operatorId;
    String operator;
    Date createTime;
    Date updateTime;
    Boolean isDeleted;
}
