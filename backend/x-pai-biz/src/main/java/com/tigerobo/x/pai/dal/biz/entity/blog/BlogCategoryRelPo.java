package com.tigerobo.x.pai.dal.biz.entity.blog;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "blog_category_rel")
public class BlogCategoryRelPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Integer blogId;
    Integer categoryId;
}
