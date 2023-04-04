package com.tigerobo.x.pai.dal.biz.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user_thumb")
public class UserThumbPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Integer userId;
    String bizId;
    Integer bizType;

    Integer actionType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    Boolean isDeleted;

    Integer notifyUserId;
}
