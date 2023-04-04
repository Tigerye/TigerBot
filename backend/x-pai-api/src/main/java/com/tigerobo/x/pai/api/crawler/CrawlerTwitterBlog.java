package com.tigerobo.x.pai.api.crawler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CrawlerTwitterBlog {
    Integer maxId;
    Integer size;
    List<CrawlerTwitterBlogItem> list;

    @Data
    public static class CrawlerTwitterBlogItem {
        Integer id;
        Integer src_id;
        String keyword;
        String author;
        //        @JSONField(name = "author_link")
        String author_link;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        Date publish_time;
        String content;
        String url;

        String reply_chain;

        String specId;

        String chat_id;
//        String json;
    }
}
