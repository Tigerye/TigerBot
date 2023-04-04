package com.tigerobo.x.pai.dal.biz.entity.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "blog_crawler")
public class BlogCrawlerPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Integer thirdId;
    String title;
    String titleCn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date pdate;
    String url;
    String ossUrl;
    String coverImage;
    String source;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date thirdCreateTime;
    Integer srcId;
    String srcName;
    String author;

    Integer processStatus;

    Integer categoryStatus;

    String errMsg;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
//    Integer vip;

}
