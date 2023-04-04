package com.tigerobo.x.pai.dal.biz.entity.pub;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "pub_search_log")
public class PubSearchLogPo {
    @Id
//    @KeySql(useGeneratedKeys = true)
    Integer id;
    Integer userId;
    String keyword;
    Integer resultNum;
    String ip;
}
