package com.tigerobo.x.pai.dal.biz.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "user_notify")
@Data
public class UserNotifyPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;

    Integer userId;

    Integer messageType;
    Integer notifyType;
    String bizId;

    String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    Boolean isDeleted;

    String messageEntity;

    String jump;

}
