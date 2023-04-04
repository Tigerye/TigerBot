package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import com.tigerobo.x.pai.api.vo.user.comment.CommentVo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserCommentPo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommentConvert {


    public static CommentVo po2vo(UserCommentPo po, Map<Integer, UserBriefVo> userBriefMap){
        if (po ==null){
            return null;
        }
        CommentVo vo = new CommentVo();

        vo.setId(po.getId());

        if (userBriefMap!=null){
            final UserBriefVo userBriefVo = userBriefMap.get(po.getUserId());
            vo.setUser(userBriefVo);
        }
        vo.setUserId(po.getUserId());
        vo.setUserAvatar(po.getUserAvatar());

        vo.setUserName(po.getUserName());

        vo.setBizId(po.getBizId());
        vo.setBizType(po.getBizType());

        vo.setContent(po.getContent());
        Integer replyRootId = po.getReplyRootId();
        vo.setReplyRootId(replyRootId);
        if (replyRootId!=null&&replyRootId>0){
            vo.setReplyUserAvatar(po.getReplyUserAvatar());
            vo.setReplyUserId(po.getReplyUserId());
            vo.setReplyUserName(po.getReplyUserName());
        }
        vo.setCreateTime(po.getCreateTime());
        return vo;
    }

    public static List<CommentVo> po2voList(List<UserCommentPo> poList,Map<Integer, UserBriefVo> userBriefMap){
        if (poList == null){
            return null;
        }
        return poList.stream().map(po->po2vo(po, userBriefMap)).collect(Collectors.toList());
    }
}
