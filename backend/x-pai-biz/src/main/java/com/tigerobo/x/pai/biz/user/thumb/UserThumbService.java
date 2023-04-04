package com.tigerobo.x.pai.biz.user.thumb;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.ThumbAction;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import com.tigerobo.x.pai.api.vo.user.comment.CommentBizVo;
import com.tigerobo.x.pai.api.vo.user.comment.CommentBizVo;
import com.tigerobo.x.pai.api.vo.user.thumb.ThumbChainVo;
import com.tigerobo.x.pai.api.vo.user.thumb.ThumbVo;
import com.tigerobo.x.pai.biz.auth.UserServiceImpl;
import com.tigerobo.x.pai.biz.biz.BusinessDetailService;
import com.tigerobo.x.pai.biz.biz.NotifyService;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.user.comment.UserCommentService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.user.UserThumbDao;
import com.tigerobo.x.pai.dal.biz.entity.user.UserThumbPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.tigerobo.x.pai.biz.user.thumb.ThumbOptService.getKey;

@Slf4j
@Service
public class UserThumbService {

    @Autowired
    private UserThumbDao userThumbUpDao;
    @Autowired
    private RedisCacheService redisCacheService;
    @Autowired
    private BusinessDetailService businessDetailService;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserCommentService userCommentService;

    @Autowired
    private NotifyService notifyService;

    public int countWaitRead(Integer userId, Date preTime){
        if (userId == null||userId<=0){
            return 0;
        }
        return userThumbUpDao.countUnRead(userId,preTime);
    }

    public PageVo<ThumbChainVo> getThumb2mePage(PageReqVo req){

        PageVo<ThumbChainVo> pageVo = new PageVo<>();
        pageVo.setPageNum(req.getPageNum());
        pageVo.setPageSize(req.getPageSize());
        final Integer userId = ThreadLocalHolder.getUserId();

        if (userId == null||userId<=0){
            return pageVo;
        }

        final List<UserThumbPo> thumb2userList = userThumbUpDao.getThumb2userList(userId, req);

        if (CollectionUtils.isEmpty(thumb2userList)){
            return pageVo;
        }
        notifyService.setThumbReadTime(userId);
        final List<ThumbChainVo> chainVos = buildThumbChainVos(thumb2userList,true);
        pageVo.setList(chainVos);

        final Page dbPage = (Page) thumb2userList;
        pageVo.setTotal(dbPage.getTotal());
        return pageVo;
    }

    public PageVo<ThumbChainVo> getMyThumbPage(PageReqVo req){

        PageVo<ThumbChainVo> pageVo = new PageVo<>();
        pageVo.setPageNum(req.getPageNum());
        pageVo.setPageSize(req.getPageSize());
        final Integer userId = ThreadLocalHolder.getUserId();

        if (userId == null||userId<=0){
            return pageVo;
        }

        final List<UserThumbPo> thumb2userList = userThumbUpDao.getuserThumbList(userId, req);

        if (CollectionUtils.isEmpty(thumb2userList)){
            return pageVo;
        }
        final List<ThumbChainVo> chainVos = buildThumbChainVos(thumb2userList,false);
        pageVo.setList(chainVos);

        final Page dbPage = (Page) thumb2userList;
        pageVo.setTotal(dbPage.getTotal());
        return pageVo;
    }

    private List<ThumbChainVo> buildThumbChainVos(List<UserThumbPo> thumb2userList,boolean needUser) {
        final List<Integer> userIds = thumb2userList.stream().map(p -> p.getUserId())
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());

        final Map<Integer, UserBriefVo> userBriefMap =needUser?
                userService.getUserBriefMap(userIds):new HashMap<>();

        Set<BusinessDetailService.BusinessReq> businessReqs = new HashSet<>();
        final Map<String,CommentBizVo> commentGridVoMap = new ConcurrentHashMap<>();

        final Map<BusinessDetailService.BusinessReq, IBusinessDetailVo> detailMap =
                buildBusinessDetail(thumb2userList, businessReqs, commentGridVoMap);

        final List<ThumbChainVo> chainVos = thumb2userList.parallelStream().map(po -> {
            ThumbChainVo chainVo = new ThumbChainVo();
            final UserBriefVo userPublicVo = userBriefMap.get(po.getUserId());
            final ThumbVo thumbVo = po2vo(po);
            thumbVo.setUser(userPublicVo);

            chainVo.setThumb(thumbVo);
            final String bizId = po.getBizId();
            final Integer bizType = po.getBizType();
            boolean isComment = BusinessEnum.COMMENT.getType().equals(bizType);
            if (isComment){
                final CommentBizVo comment = commentGridVoMap.get(bizId);
                if (comment!=null){
                    chainVo.setComment(comment);
                    BusinessDetailService.BusinessReq businessReq = new BusinessDetailService
                            .BusinessReq(comment.getDataBizId(),comment.getDataBizType());
                    final IBusinessDetailVo iBusinessDetailVo = detailMap.get(businessReq);
                    if (iBusinessDetailVo!=null){
                        chainVo.setBusinessDetail(iBusinessDetailVo);
                    }
                }
            }else {
                BusinessDetailService.BusinessReq br = new BusinessDetailService.BusinessReq(bizId, po.getBizType());
                final IBusinessDetailVo detail = detailMap.get(br);
                chainVo.setBusinessDetail(detail);
            }
            return chainVo;
        }).collect(Collectors.toList());

        return chainVos;
    }

    private Map<BusinessDetailService.BusinessReq, IBusinessDetailVo> buildBusinessDetail(
            List<UserThumbPo> thumb2userList,
            Set<BusinessDetailService.BusinessReq> businessReqs,
            Map<String, CommentBizVo> commentGridVoMap
    ) {

        Set<Integer> commentIds = new HashSet<>();
        for (UserThumbPo po : thumb2userList) {
            final Integer bizType = po.getBizType();
            if (BusinessEnum.COMMENT.getType().equals(bizType)){
                if (po.getBizId().matches("\\d+")){
                    commentIds.add(Integer.parseInt(po.getBizId()));
                }
            }else {
                BusinessDetailService.BusinessReq br = new BusinessDetailService
                        .BusinessReq(po.getBizId(),po.getBizType());
                businessReqs.add(br);
            }
        }

        if (commentIds.size()>0){
            final List<CommentBizVo> commentGridVos = userCommentService.getByIds(new ArrayList<>(commentIds));
            if (commentGridVos!=null){
                for (CommentBizVo commentGridVo : commentGridVos) {
                    final Integer id = commentGridVo.getId();
                    commentGridVoMap.put(String.valueOf(id),commentGridVo);
                }
            }
        }
        if (commentGridVoMap.size()>0){
            for (CommentBizVo value : commentGridVoMap.values()) {
                BusinessDetailService.BusinessReq br = new BusinessDetailService
                        .BusinessReq(value.getDataBizId(),value.getDataBizType());
                businessReqs.add(br);
            }
        }

        final Map<BusinessDetailService.BusinessReq, IBusinessDetailVo> detailMap =
                businessDetailService.getReqDetailMap(businessReqs);
        return detailMap;
    }

    private ThumbVo po2vo(UserThumbPo po){
        ThumbVo vo = new ThumbVo();

        vo.setBizId(po.getBizId());
        vo.setBizType(po.getBizType());

        final BusinessEnum byType = BusinessEnum.getByType(po.getBizType());
        if (byType!=null){
            vo.setBizName(byType.getText());
        }

        vo.setId(po.getId());
        vo.setCreateTime(po.getCreateTime());

        return vo;
    }
    public List<UserThumbPo> getUserList(Integer userId, List<String> bizIds, Integer bizType) {
        return getUserList(userId,bizIds,bizType,ThumbAction.THUMB_UP);
    }
    public List<UserThumbPo> getUserList(Integer userId, List<String> bizIds, Integer bizType,ThumbAction action) {

        if (userId == null||CollectionUtils.isEmpty(bizIds)||bizType == null||action == null){
            return null;
        }
        final List<UserThumbPo> list = userThumbUpDao.getList(userId, bizIds, bizType);
        if (CollectionUtils.isEmpty(list)){
            return list;
        }
        return list.stream().filter(p->action.getType().equals(p.getActionType())).collect(Collectors.toList());
    }

    public int countByDetail(String bizId, Integer type){
        return count(bizId,type);
    }

    public int countThumbUp(String bizId, Integer type) {
        return count(bizId,type);
    }
    public int count(String bizId, Integer type) {
        return count(bizId,type,0);
    }

    public int count(String bizId, Integer type,Integer actionType) {
        String key = getKey(bizId, type,actionType);
        int count = redisCacheService.getNum(key);
        return Math.max(count, 0);
    }


    public int countBlog(Integer id) {
        return count(String.valueOf(id),BusinessEnum.BLOG.getType());
    }
}
