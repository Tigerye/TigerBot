package com.tigerobo.x.pai.dal.biz.entity.github;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.dal.base.handler.Boolean2TypeHandler;
import lombok.Data;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "github_repo_info")
public class GithubRepoInfoPo {
    @Id
    Integer id;
    String repoUid;
    Integer userId;
//    @Column(name = "update_dt")
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
//    @ColumnType(jdbcType = JdbcType.TINYINT, typeHandler = Boolean2TypeHandler.class)
    Boolean isDeleted;
    String aiTag;
}
