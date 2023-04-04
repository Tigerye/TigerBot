package com.tigerobo.x.pai.api.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryDto;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogChatVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BlogAdminVo {

    Integer id;
    Integer userId;
    String title;
    String translateTitle;
    String summary;
    String translateSummary;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    Date publishTime;
    String ossUrl;
    String translateUrl;
    String zhenUrl;

    String headImg;
    String sourceUrl;

    String sourceName;
    String sourceLogo;

    String authorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    Integer onlineStatus;

    Integer lagType;

    Integer sourceFrom;
    Integer siteId;
//    PubSiteVo site;
    List<BlogCategoryDto> blogCategoryList;
    String content;
    String translateContent;
    Integer bigShotId;
    int viewNum;
    int shareNum;
    int commentNum;
    int thumbUpNum;
    Boolean hasReplyChain;
    List<BlogChatVo> replyList;
    Integer vip;

    Integer isDeleted;

    Integer recommend;
    Integer tagType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date recommendTime;

}
