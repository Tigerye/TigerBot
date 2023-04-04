package com.tigerobo.x.pai.dal.admin.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "`sso_user`")
@Data
public class SsoUserPo {
    @Id
    @KeySql(useGeneratedKeys = true)

    Integer id;
    String userName;
    String password;
    Boolean isDeleted;
}
