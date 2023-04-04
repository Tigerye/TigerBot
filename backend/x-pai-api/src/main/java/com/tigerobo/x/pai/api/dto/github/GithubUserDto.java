package com.tigerobo.x.pai.api.dto.github;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class GithubUserDto {
    Integer id;
    Integer userId ;
    String userName ;
    String userType ;
    String htmlUrl ;
    String avatarUrl ;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date userCreatedTime ;
    String company ;
    String blog ;
    String location ;
    String email ;
    String subTile ;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime ;
}
