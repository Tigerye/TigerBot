package com.tigerobo.x.pai.dal.biz.entity.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "blog_bigshot_reply")
public class BlogBigshotReplyPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;

    Integer blogId;

    Integer seq;

    String author;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date publishTime;

    String content;

    String translateContent;

    Boolean currentBlog;

    String specId;
    Boolean isDeleted;

    String summary;
    String translateSummary;

    String chatId;
    String headImg;
}
