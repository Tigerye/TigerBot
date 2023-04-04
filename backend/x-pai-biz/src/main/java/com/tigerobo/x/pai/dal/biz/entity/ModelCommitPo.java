package com.tigerobo.x.pai.dal.biz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "model_commit")
@Data
public class ModelCommitPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id ;
    Integer userId;
    String modelId;
    String name;
    String memo;
    String filePath;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;
    Boolean isDeleted;
    Long size;
}
