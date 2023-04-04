package com.tigerobo.x.pai.dal.biz.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user_commit_site")
public class UserCommitSitePo {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    Integer userId;
    String name;
    String url;
    String memo;
    Boolean isDeleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

    Integer status;
    String msg;

    Integer mediaType;
    Integer mediaId;
}
