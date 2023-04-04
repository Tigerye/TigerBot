package com.tigerobo.x.pai.dal.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "member")
@Data
public class MemberPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    Integer userId;
    private Date expireDate;
    Integer level;
    Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    private Boolean isDeleted;
}
