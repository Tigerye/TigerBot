package com.tigerobo.x.pai.dal.biz.entity.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "`blog_chat`")
public class BlogChatPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Integer blogId;
    String chatId;
    String specId;
    String author;
    Integer bigShotId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date publishTime;
    String content;
    String translateContent;
    String headImg;
    String summary;
    String translateSummary;

    Boolean isDeleted;
    Boolean mainChat;

}
