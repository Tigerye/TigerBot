package com.tigerobo.x.pai.dal.biz.entity.blog;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "crawler_non_academic")
public class BlogTwitterNoAcademicPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private Long twitterId;
    private Long fanNum;
    private String area;

    private String nickname;
    private  String twitterImg;
    private String account;
    private String twitterUrl;
    private String imgUrl;

}
