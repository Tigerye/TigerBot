package com.tigerobo.x.pai.dal.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "ai_user_interact")
@Data
public class AiUserInteractPo {

    @Id
    private Integer id;
    String bizId;
    Integer bizType;


    Integer userId;
    String title;

    Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date publishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    Boolean isDeleted;
}
