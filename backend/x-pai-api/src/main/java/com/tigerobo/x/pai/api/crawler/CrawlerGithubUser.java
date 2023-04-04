package com.tigerobo.x.pai.api.crawler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class CrawlerGithubUser {
    Integer maxId;
    Integer minId;
    Integer size;
    List<CrawlerGithubUser.CrawlerGithubUserItem> list;

    @Data
    public static class CrawlerGithubUserItem {
        Integer id ;
        Integer user_id ;
        String user_name ;
        String user_type ;
        String html_url ;
        String avatar_url ;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        Date user_created_at ;
        String company ;
        String blog ;
        String location ;
        String email ;
        String sub_tile ;
        Integer src_id ;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        Date create_time ;

    }
}
