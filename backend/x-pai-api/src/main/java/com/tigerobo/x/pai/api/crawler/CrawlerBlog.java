package com.tigerobo.x.pai.api.crawler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CrawlerBlog {
    Integer maxId;
    Integer size;
    List<CrawlerBlogItem> list;

    @Data
    public static class
    CrawlerBlogItem {
        Integer id;
        String title;
        String title_cn;
        @JsonFormat(pattern = "yyyy-MM-dd" ,timezone = "GMT+8")
        Date pdate;
        String url;
        String oss_url;
        String cover_image;
        String source;
        String src_name;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
        Date createTime;
        Integer src_id;
        String author;

        transient Integer siteId;
    }
}
