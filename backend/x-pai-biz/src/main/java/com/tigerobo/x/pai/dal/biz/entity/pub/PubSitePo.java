package com.tigerobo.x.pai.dal.biz.entity.pub;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "pub_site")
public class PubSitePo {

    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;

    String name;

    String logoOss;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

    String intro;

    Boolean isDeleted;

    Integer score;

    Integer vip;
    Integer articleCount;

}
