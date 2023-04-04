package com.tigerobo.x.pai.biz.auth;

import com.tigerobo.x.pai.api.enums.ThumbAction;
import com.tigerobo.x.pai.api.vo.biz.interact.InteractVo;
import com.tigerobo.x.pai.biz.biz.ViewCountService;
import com.tigerobo.x.pai.biz.biz.log.ShareLogService;
import com.tigerobo.x.pai.biz.user.comment.BusinessCommentService;
import com.tigerobo.x.pai.biz.user.thumb.UserThumbService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.ThreadUtil;
import com.tigerobo.x.pai.dal.biz.entity.user.UserThumbPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class InteractionService {

    @Autowired
    private UserThumbService userThumbService;
    @Autowired
    private BusinessCommentService businessCommentService;
    @Autowired
    private ShareLogService shareLogService;

    @Autowired
    private ViewCountService viewCountService;

    public Map<String,InteractVo> completeCount(List<String> bizIds, Integer bizType) {
        if (CollectionUtils.isEmpty(bizIds)||bizType == null) {
            return null;
        }
        List<InteractVo> list = new ArrayList<>();
        ThreadUtil.detailPool.submit(()->{
            List<InteractVo> subList = bizIds.parallelStream().map(bizId -> {
                InteractVo interactVo = new InteractVo();
                interactVo.setBizId(bizId);
                interactVo.setBizType(bizType);
                try {
                    int view = viewCountService.getView(bizType, bizId);
                    interactVo.setViewNum(view);
                    final int thumbUpNum = userThumbService.countThumbUp(bizId, bizType);
                    interactVo.setThumbUpNum(thumbUpNum);
//                blogVo.setThumbDownNum(userThumbService.countBlogThumbDown(id));
                    int commentNum = businessCommentService.countFromCache(bizId, bizType);
                    interactVo.setCommentNum(commentNum);
                    int shareNum = shareLogService.getCount(bizId, bizType);

                    interactVo.setShareNum(shareNum);
                } catch (Exception ex) {
                    log.error("completeCount:{},bizType:{}", interactVo.getBizId(), interactVo.getBizType(), ex);
                }
                return interactVo;
            }).collect(Collectors.toList());
            if (subList.size()>0){
                list.addAll(subList);
            }
        }).join();



        List<String> idStrList = list.stream().map(vo -> String.valueOf(vo.getBizId())).collect(Collectors.toList());
        Integer userId = ThreadLocalHolder.getUserId();

        List<UserThumbPo> upList = userThumbService.getUserList(userId, idStrList, bizType, ThumbAction.THUMB_UP);

//        Set<String> downBizIdList = CollectionUtils.isEmpty(downList) ? new HashSet<>() : downList.stream().map(d -> d.getBizId()).collect(Collectors.toSet());
        Set<String> upBizIdList = CollectionUtils.isEmpty(upList) ? new HashSet<>() : upList.stream().map(d -> d.getBizId()).collect(Collectors.toSet());

        for (InteractVo interactVo : list) {
            String id = interactVo.getBizId();
            if (upBizIdList.contains(id)) {
                interactVo.setThumbUp(true);
            }
        }

        Map<String,InteractVo> map = new HashMap<>();

        for (InteractVo interactVo : list) {
            map.put(interactVo.getBizId(),interactVo);
        }
        return map;
    }

    public InteractVo completeCount(String bizId,Integer bizType) {
        if (bizId == null||bizType == null){
            return null;
        }

        InteractVo interactVo = new InteractVo();
        interactVo.setBizId(bizId);
        interactVo.setBizType(bizType);

        int thumbUpCount = userThumbService.countThumbUp(bizId,bizType);
        interactVo.setThumbUpNum(thumbUpCount);

        int blogCount = businessCommentService.countOnBizId(bizId, bizType);

        interactVo.setCommentNum(blogCount);
        int shareNum = shareLogService.getCount(bizId, bizType);
        interactVo.setShareNum(shareNum);

        Integer userId = ThreadLocalHolder.getUserId();

        List<UserThumbPo> thumbUpList = userThumbService.getUserList(userId,
                Arrays.asList(bizId), bizType, ThumbAction.THUMB_UP);

        if (!CollectionUtils.isEmpty(thumbUpList)) {
            interactVo.setThumbUp(true);
        }

        int commentNum = businessCommentService.countUserCommentNum(userId, bizId, bizType);
        interactVo.setUserHasComment(commentNum > 0);

        return interactVo;
    }

}
