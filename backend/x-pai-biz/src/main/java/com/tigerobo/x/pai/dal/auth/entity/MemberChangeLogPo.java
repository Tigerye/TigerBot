package com.tigerobo.x.pai.dal.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "member_change_log")
@Data
public class MemberChangeLogPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;

    Long orderNo;
    Integer userId;

    String name;
    @Column(name = "`desc`")
    String desc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date preTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date expireTime;

    Integer memberLevel;
    Integer days;

    Integer amount;
    BigDecimal totalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

}
