package com.tigerobo.x.pai.dal.biz.entity.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "blog_info")
public class BlogInfoPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Integer userId;
    String title;
    String translateTitle;
    String summary;
    String translateSummary;
    String sourceUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date publishTime;
    Integer thirdId;
    Integer sourceFrom;

    String ossUrl;
    String translateUrl;
    String zhenUrl;

    String headImg;

    String sourceName;

    Integer onlineStatus;

    Integer processStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

    Boolean isDeleted;

    String authorName;

    Integer siteId;

    String category;

    String content;
    String translateContent;
    Integer bigShotId;

    Boolean hasReplyChain;

    Integer vip;

    String specId;

    Integer recommend;
    Integer tagType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date recommendTime;
}
