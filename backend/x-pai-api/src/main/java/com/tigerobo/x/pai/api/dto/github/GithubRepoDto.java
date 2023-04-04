package com.tigerobo.x.pai.api.dto.github;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GithubRepoDto {

    Integer id;
    String repoUid;
    Integer userId;
    Integer updateDt;

    String reposName;
    String url;
    String description;
    String language;
    List<String> tagList;
    String license;
    Integer forksCount;
    String starCount;
    Integer openIssues;
    String needHelpIssues;
    Integer openPulls;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date reposUpdate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
//    String aiTag;
    List<String> aiTagUidList;
    Boolean isDeleted;
}
