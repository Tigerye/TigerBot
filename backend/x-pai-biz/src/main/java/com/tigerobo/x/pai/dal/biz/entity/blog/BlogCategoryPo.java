package com.tigerobo.x.pai.dal.biz.entity.blog;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "blog_category")
public class BlogCategoryPo{
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    String name;
    Boolean isDeleted;
}
