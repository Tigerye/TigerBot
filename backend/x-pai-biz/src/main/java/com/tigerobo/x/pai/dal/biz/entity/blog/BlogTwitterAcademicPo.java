package com.tigerobo.x.pai.dal.biz.entity.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name="crawler_academic")
public class BlogTwitterAcademicPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private String crawlerId;
    private String name;
    private String nameZh;
    private String position;
    private String twitter;
    private String twitterImg;
    private String twitterDesc;
    private String org;
    private Integer paperNum;
    private Integer paperQuoteNum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private boolean isDeleted;
    private String logo;
    private String imgUrl;
}
