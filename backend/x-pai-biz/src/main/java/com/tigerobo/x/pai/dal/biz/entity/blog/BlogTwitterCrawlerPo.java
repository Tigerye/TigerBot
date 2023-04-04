package com.tigerobo.x.pai.dal.biz.entity.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "blog_twitter_crawler")
public class BlogTwitterCrawlerPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Integer thirdId;
    Integer srcId;
    String keyword;
    String author;
    String authorLink;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date publishTime;
    String content;
    String url;

    Integer processStatus;
    String replyChain;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    String specId;
    private String chatId;

    private String errMsg;
    Integer blogId;
}
