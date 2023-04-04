package com.tigerobo.x.pai.api.vo.user.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentVo {

    String title;

    Integer id;

    UserBriefVo user;
    @Deprecated
    Integer userId;
    @Deprecated
    String userName;
    @Deprecated
    String userAvatar;

    String bizId;
    Integer bizType;
    String content;



    Integer replyCommentId;
    Integer replyRootId;
    Integer replyUserId;
    String replyUserName;
    String replyUserAvatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    Integer thumbUpNum;
    Integer thumbDownNum;

    List<CommentVo> subCommentList;

    boolean thumbUp;
    boolean thumbDown;
}
