package com.tigerobo.x.pai.dal.biz.entity.blog;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "blogger")
public class BloggerPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Integer userId;
    Integer score;

    Boolean isDeleted;
    Boolean recommend;
}
