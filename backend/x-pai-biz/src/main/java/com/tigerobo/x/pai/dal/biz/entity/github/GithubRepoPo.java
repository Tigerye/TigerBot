package com.tigerobo.x.pai.dal.biz.entity.github;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
@Data
@Table(name = "github_repo")
public class GithubRepoPo {
    @Id
    Integer id;
    String uid;
    Integer userId;
    Integer updateDt;

    String reposName;
    String url;
    String description;
    String language;
    String tags;
    String license;
    Integer forksCount;
    String starCount;
    Integer openIssues;
    String needHelpIssues;
    Integer openPulls;
    @Column(name ="repos_update_at" )
    @ColumnType(jdbcType = JdbcType.DATE, typeHandler = DateTypeHandler.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date reposUpdate;

    Integer srcId;
    Integer thirdId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    Integer isDeleted;
}
