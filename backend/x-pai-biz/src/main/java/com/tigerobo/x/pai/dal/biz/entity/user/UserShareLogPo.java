package com.tigerobo.x.pai.dal.biz.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user_share_log")
public class UserShareLogPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    String bizId;
    Integer bizType;
    Integer sharer;
    Integer clicker;
    String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
}
