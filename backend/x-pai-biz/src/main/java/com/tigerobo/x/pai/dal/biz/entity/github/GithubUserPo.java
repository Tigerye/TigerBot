package com.tigerobo.x.pai.dal.biz.entity.github;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "github_user")
public class GithubUserPo {
    @Id
    Integer id ;
    Integer userId ;
    String userName ;
    String userType ;
    String htmlUrl ;
    String avatarUrl ;
    @Column(name="user_created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date userCreatedTime ;
    String company ;
    String blog ;
    String location ;
    String email ;
    String subTile ;
    Integer srcId ;
    Integer thirdId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime ;
    Integer isDeleted;
}
