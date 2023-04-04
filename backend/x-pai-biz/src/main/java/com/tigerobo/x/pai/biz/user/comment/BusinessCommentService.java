package com.tigerobo.x.pai.biz.user.comment;

import com.github.pagehelper.Page;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.ThumbAction;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import com.tigerobo.x.pai.api.vo.user.comment.CommentQueryReq;
import com.tigerobo.x.pai.api.vo.user.comment.CommentVo;
import com.tigerobo.x.pai.biz.auth.UserServiceImpl;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.converter.CommentConvert;
import com.tigerobo.x.pai.biz.user.thumb.UserThumbService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.user.UserCommentDao;
import com.tigerobo.x.pai.dal.biz.entity.user.UserCommentPo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserThumbPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.tigerobo.x.pai.biz.user.comment.CommentOptService.getCountKey;

/**
 * 业务下的评论服务
 */
@Slf4j
@Service
public class BusinessCommentService {


    @Autowired
    private UserCommentDao userCommentDao;
    @Autowired
    private UserThumbService userThumbService;
    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private UserServiceImpl userService;

    public int countOnBizId(String bizId, Integer bizType){
        int count = userCommentDao.count(bizId, bizType);
        if (count>0){
            resetCacheCount(bizId,bizType,count);
        }
        return count;
    }

    public int countUserCommentNum(Integer userId,String bizId,Integer bizType){
        return userCommentDao.countUserCommentNum(userId,bizId,bizType);
    }

    public PageVo<CommentVo> getBizCommentList(CommentQueryReq req) {
        String bizId = req.getBizId();
        Preconditions.checkArgument(bizId !=null,"参数为空");
        Integer bizType = req.getBizType();
        Preconditions.checkArgument(bizType !=null,"参数为空");

        PageVo<CommentVo> page = new PageVo<>();

        List<UserCommentPo> list = userCommentDao.getBizCommentListById(bizId, bizType, req.getPageNum(), req.getPageSize());

        if (CollectionUtils.isEmpty(list)){
            return page;
        }
        Page<UserCommentPo> poPage = (Page<UserCommentPo>)list;
        List<Integer> ids = list.stream().map(po -> po.getId()).collect(Collectors.toList());


        List<UserCommentPo> subCommentList = userCommentDao.getSubCommentList(bizId, bizType, ids);
        Map<Integer,List<UserCommentPo>> subMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(subCommentList)){
            for (UserCommentPo commentPo : subCommentList) {
                Integer replyRootId = commentPo.getReplyRootId();
                List<UserCommentPo> userCommentPos = subMap.computeIfAbsent(replyRootId, k -> new ArrayList());

                userCommentPos.add(commentPo);
            }
        }

        Set<Integer> userIds = new HashSet<>();

        for (UserCommentPo userCommentPo : list) {
            if (userCommentPo.getUserId()!=null&&userCommentPo.getUserId()>0){
                userIds.add(userCommentPo.getUserId());
            }
        }

        if (!CollectionUtils.isEmpty(subCommentList)){

            for (UserCommentPo userCommentPo : subCommentList) {
                if (userCommentPo.getUserId()!=null&&userCommentPo.getUserId()>0){
                    userIds.add(userCommentPo.getUserId());
                }
            }
        }
        final Map<Integer, UserBriefVo> userBriefMap = userService.getUserBriefMap(userIds);

        Integer userId = ThreadLocalHolder.getUserId();

        List<CommentVo> commentVoList = new ArrayList<>();
        for (UserCommentPo commentPo : list) {

            CommentVo vo = CommentConvert.po2vo(commentPo,userBriefMap);
            completeCount(vo);
            Integer id = commentPo.getId();
            List<UserCommentPo> userCommentPos = subMap.get(id);

            List<CommentVo> subList = buildSubList(userCommentPos,userBriefMap);
            vo.setSubCommentList(subList);
            commentVoList.add(vo);
        }

        completeThumb(userId, commentVoList);

        page.setTotal(poPage.getTotal());
        page.setList(commentVoList);
        page.setPageSize(req.getPageSize());
        page.setPageNum(req.getPageNum());

        return page;
    }

    private void completeThumb(Integer userId, List<CommentVo> commentVoList) {
        List<String> idStrList = new ArrayList<>();
        for (CommentVo vo : commentVoList) {
            idStrList.add(String.valueOf(vo.getId()));
            List<CommentVo> subList = vo.getSubCommentList();
            if (!CollectionUtils.isEmpty(subList)){
                for (CommentVo commentVo : subList) {
                    idStrList.add(String.valueOf(commentVo.getId()));
                }
            }
        }
        List<UserThumbPo> upList = userThumbService.getUserList(
                userId, idStrList, BusinessEnum.COMMENT.getType(),ThumbAction.THUMB_UP);
        List<UserThumbPo> downList = userThumbService.getUserList(
                userId, idStrList, BusinessEnum.COMMENT.getType(),ThumbAction.THUMB_DOWN);


        Set<String> upIds = CollectionUtils.isEmpty(upList)?new HashSet<>():upList.stream().map(d->d.getBizId()).collect(Collectors.toSet());
        Set<String> downIds = CollectionUtils.isEmpty(downList)?new HashSet<>():upList.stream().map(d->d.getBizId()).collect(Collectors.toSet());

        for (CommentVo vo : commentVoList) {

            Integer id = vo.getId();

            if (upIds.contains(String.valueOf(id))){
                vo.setThumbUp(true);
            }
            if (downIds.contains(String.valueOf(id))){
                vo.setThumbDown(true);
            }

            List<CommentVo> subList = vo.getSubCommentList();
            if (!CollectionUtils.isEmpty(subList)){
                for (CommentVo commentVo : subList) {
                    if (upIds.contains(String.valueOf(commentVo.getId()))){
                        commentVo.setThumbUp(true);
                    }
                    if (downIds.contains(String.valueOf(commentVo.getId()))){
                        commentVo.setThumbDown(true);
                    }
                }
            }
        }
    }

    private  List<CommentVo> buildSubList(List<UserCommentPo> userCommentPos,Map<Integer, UserBriefVo> userBriefMap){
        List<CommentVo> subList = CommentConvert.po2voList(userCommentPos,userBriefMap);

        if (CollectionUtils.isEmpty(subList)){
            return null;
        }

        subList.parallelStream().forEach(vo->{
            completeCount(vo);
        });
        return subList;
    }

    private void completeCount(CommentVo vo) {
        Integer id = vo.getId();

        int upCount = userThumbService.count(String.valueOf(id), BusinessEnum.COMMENT.getType());

        int downCount = userThumbService.count(String.valueOf(id), BusinessEnum.COMMENT.getType(),1);
        vo.setThumbUpNum(upCount);
        vo.setThumbDownNum(downCount);
    }


    public int countFromCache(String bizId,Integer type){

        String countKey = getCountKey(bizId, type);
        if (countKey == null){
            return 0;
        }
        String value = redisCacheService.get(countKey);
        if (StringUtils.isEmpty(value)||!value.matches("\\d+")){
            return 0;
        }
        return Integer.parseInt(value);
    }
    private void resetCacheCount(String id, Integer type,int value) {
        String key = getCountKey(id, type);
        if (key == null){
            return;
        }
        redisCacheService.set(key,String.valueOf(value));
    }

}
