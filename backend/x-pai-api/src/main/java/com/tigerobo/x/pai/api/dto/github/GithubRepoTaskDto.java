package com.tigerobo.x.pai.api.dto.github;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class GithubRepoTaskDto {
    Integer id;

    Integer type;
    String url;
    Integer sysUserId;

    Integer status;
    String msg;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;
    Integer isDeleted;
}
