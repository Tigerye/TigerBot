package com.tigerobo.x.pai.api.vo.biz.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BlogChatVo {
    String chatId;

    String headImg;

    Integer bigShotId;
    String logoOss;

    Integer blogId;
    Integer seq;

    String author;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date publishTime;

    String content;

    String translateContent;

    String summary;
    String translateSummary;

    Boolean currentBlog;
    Boolean mainChat;

}
