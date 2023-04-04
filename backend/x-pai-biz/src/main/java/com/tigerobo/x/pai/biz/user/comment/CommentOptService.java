package com.tigerobo.x.pai.biz.user.comment;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.user.comment.UserCommentAddReq;
import com.tigerobo.x.pai.biz.biz.BusinessDetailService;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.user.UserCommentDao;
import com.tigerobo.x.pai.dal.biz.entity.user.UserCommentPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class CommentOptService {


    @Autowired
    private UserCommentDao userCommentDao;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private BusinessDetailService businessDetailService;

    public void add(UserCommentAddReq req) {
        Preconditions.checkArgument(req != null, "参数为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(req.getBizId()), "业务id为空");
        Preconditions.checkArgument(req.getBizType() != null, "业务id为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(req.getContent()), "评论为空");
        Integer userId = ThreadLocalHolder.getUserId();
        User user = userService.getFromCache(userId);
        if (user == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }

        String bizId = req.getBizId();
        Integer bizType = req.getBizType();

        final IBusinessDetailVo detail = businessDetailService.getDetail(bizType, bizId);


        Integer replyCommentId = req.getReplyCommentId();
        UserCommentPo commentAddPo = new UserCommentPo();

        commentAddPo.setUserId(userId);
        commentAddPo.setUserAvatar(user.getAvatar());
        commentAddPo.setUserName(user.getName());

        commentAddPo.setBizId(req.getBizId());
        commentAddPo.setBizType(req.getBizType());
        commentAddPo.setContent(req.getContent());

        commentAddPo.setReplyRootId(0);
        final boolean isReply = replyCommentId != null && replyCommentId > 0;

        if (detail!=null){
            final BusinessEnum byType = BusinessEnum.getByType(bizType);
            if (byType!=null&&byType.isCanNotify()){
                commentAddPo.setNotifyUserId(detail.getUserId());
            }
        }
        if (isReply) {

            UserCommentPo replyComment = userCommentDao.load(replyCommentId);
            if (replyComment == null) {
                throw new IllegalArgumentException("回复评论不存在");
            }
            Integer replyRootId = 0;
            if (replyComment.getReplyRootId() != null && replyComment.getReplyRootId() > 0) {
                replyRootId = replyComment.getReplyRootId();
            } else {
                replyRootId = replyComment.getId();
            }
            commentAddPo.setReplyUserAvatar(replyComment.getUserAvatar());
            commentAddPo.setReplyUserId(replyComment.getUserId());
            commentAddPo.setReplyUserName(replyComment.getUserName());
            commentAddPo.setReplyCommentId(replyCommentId);
            commentAddPo.setReplyRootId(replyRootId);
            commentAddPo.setNotifyUserId(replyComment.getUserId());
        }
        commentAddPo.setOnlineStatus(1);

        userCommentDao.add(commentAddPo);

        incr(commentAddPo.getBizId(),commentAddPo.getBizType());
    }

    public void delete(Integer id) {

        UserCommentPo load = userCommentDao.load(id);
        if (load == null) {
            return;
        }
        Integer userId = ThreadLocalHolder.getUserId();
        if (!Objects.equals(load.getUserId(), userId)) {
            throw new AuthorizeException(ResultCode.USER_NO_PERMISSION);
        }
        userCommentDao.delete(id);
        if (load.getReplyRootId()!=null&&load.getReplyRootId()==0){
            userCommentDao.updateSubComment(load.getBizId(),load.getBizType(),id);
        }

        decr(load.getBizId(),load.getBizType());
    }



    private void incr(String id, Integer type) {
        String key = getCountKey(id, type);
        if (key == null){
            return;
        }
        redisCacheService.incr(key);
    }

    private void decr(String id, Integer type) {

        String key = getCountKey(id, type);
        if (key == null){
            return;
        }
        redisCacheService.decr(key);
    }

    static String getCountKey(String bizId,Integer bizType){
        if (bizId == null||bizId.isEmpty()||bizType == null){
            return null;
        }
        return "pai:comment:biz:"+bizType+":"+bizId;
    }
}
