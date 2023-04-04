package com.tigerobo.x.pai.api.vo.biz.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryDto;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryRelDto;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubBigShotVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BlogVo implements IBusinessDetailVo {
    int bizType = BusinessEnum.BLOG.getType();
    Integer id;
    Integer userId;
    User user;
    String title;
    String translateTitle;
    String summary;
    String translateSummary;

    String enTitle;
    String enSummary;

    String zhTitle;
    String zhSummary;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    Date publishTime;

    String ossUrl;
    String translateUrl;

    String enUrl;
    String zhUrl;
    String zhenUrl;

    String headImg;

    String sourceUrl;
    String sourceName;
    String sourceLogo;

    String authorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date createTime;


    Integer lagType;

    Integer sourceFrom;

    Boolean isNew;

    transient Integer siteId;

    PubSiteVo site;

    Integer onlineStatus;
    /**
     * @see com.tigerobo.x.pai.api.auth.entity.Role
     */
    String role;

    boolean follow;

//    List<String> categoryList;
    List<BlogCategoryDto> categoryList;

    String content;
    String translateContent;
    PubBigShotVo bigShot;
    Integer bigShotId;
    Boolean hasReplyChain;

    int viewNum;

    int shareNum;

    int commentNum;

    int thumbUpNum;

    @ApiModelProperty(value = "用户是否评论")
    boolean userHasComment;

    boolean thumbUp;
    boolean canView =true;

    List<BlogChatVo> replyList;

    Integer vip;


}
