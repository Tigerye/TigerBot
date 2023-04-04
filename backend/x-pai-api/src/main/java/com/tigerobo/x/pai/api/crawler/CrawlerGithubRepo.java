package com.tigerobo.x.pai.api.crawler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CrawlerGithubRepo {
    Integer maxId;
    Integer minId;
    Integer size;
    List<CrawlerGithubRepoItem> list;

    @Data
    public static class CrawlerGithubRepoItem {
        Integer id;
        String uid;
        Integer user_id;

        Integer update_dt;
        String repos_name;
        String url;
        String description;
        String language;
        String tags;
        String license;
        Integer forks_count;
        String star_count;
        Integer open_issues;
        String need_help_issues;
        Integer open_pulls;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        Date repos_update_at;

        Integer src_id;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        Date create_tim;
//        String json;
    }
}
