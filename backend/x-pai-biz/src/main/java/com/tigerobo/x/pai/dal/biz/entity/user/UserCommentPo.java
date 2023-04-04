package com.tigerobo.x.pai.dal.biz.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user_comment")
public class UserCommentPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Integer userId;
    String userName;
    String userAvatar;

    String bizId;
    Integer bizType;
    String content;

    Integer likeNum;
    Integer appendNum;

    Integer replyCommentId;
    Integer replyRootId;
    Integer replyUserId;
    String replyUserName;
    String replyUserAvatar;

    Integer onlineStatus;

    Boolean isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    Integer notifyUserId;
}
