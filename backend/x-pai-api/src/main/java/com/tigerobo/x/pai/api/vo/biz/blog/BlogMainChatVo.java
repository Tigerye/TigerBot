package com.tigerobo.x.pai.api.vo.biz.blog;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryDto;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubBigShotVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BlogMainChatVo implements IBusinessDetailVo {
    int bizType = BusinessEnum.BLOG_CHAT.getType();
    String chatId;

    Integer blogId;

    String content;
    String translateContent;

    String summary;
    String translateSummary;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    Date publishTime;

    String headImg;


//    String authorName;


    boolean follow;

    Integer bigShotId;
    String sourceName;
    String sourceLogo;

    Boolean hasReplyChain;


    int viewNum;

    int shareNum;

    int commentNum;

    int thumbUpNum;
    @ApiModelProperty(value = "用户是否评论")
    boolean userHasComment;
    boolean thumbUp;

    List<BlogChatVo> replyList;


}
