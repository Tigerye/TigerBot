package com.tigerobo.x.pai.dal.biz.entity.pub;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "pub_crawler_site_rel")
public class PubCrawlerSiteRelPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
//    String name;

    Integer srcId;
    Integer siteId;
}
