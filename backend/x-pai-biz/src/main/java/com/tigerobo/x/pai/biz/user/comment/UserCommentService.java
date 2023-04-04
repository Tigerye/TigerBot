package com.tigerobo.x.pai.biz.user.comment;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import com.tigerobo.x.pai.api.vo.user.comment.CommentBizVo;
import com.tigerobo.x.pai.api.vo.user.comment.CommentChainVo;
import com.tigerobo.x.pai.api.vo.user.comment.CommentGridVo;
import com.tigerobo.x.pai.biz.auth.UserServiceImpl;
import com.tigerobo.x.pai.biz.biz.BusinessDetailService;
import com.tigerobo.x.pai.biz.biz.IBusinessDetailFetchService;
import com.tigerobo.x.pai.biz.biz.NotifyService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.ThreadUtil;
import com.tigerobo.x.pai.dal.biz.dao.user.UserCommentDao;
import com.tigerobo.x.pai.dal.biz.entity.user.UserCommentPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserCommentService implements IBusinessDetailFetchService {
    @Autowired
    private UserCommentDao userCommentDao;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private BusinessDetailService businessDetailService;

    @Autowired
    private NotifyService notifyService;

    public PageVo<CommentChainVo> getCommentMeList(UserPageReq req){
        PageVo<CommentChainVo> page = new PageVo();
        page.setPageNum(req.getPageNum());
        page.setPageSize(req.getPageSize());
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null||userId==0){
            return page;
        }

        final List<UserCommentPo> pos = userCommentDao.getNotifyUserCommentList(userId, req);
        if (CollectionUtils.isEmpty(pos)){
            return page;
        }
        notifyService.setCommentReadTime(userId);
        List<CommentChainVo> chainVos = buildChainList(pos);
        final Page dbPage = (Page) pos;
        page.setTotal(dbPage.getTotal());
        page.setList(chainVos);
        return page;
    }

    public int countWaitReadComment(Integer userId, Date preTime){
        if (userId == null||userId<=0){
            return 0;
        }
        return userCommentDao.countUnRead(userId,preTime);
    }

    public PageVo<CommentChainVo> getMyCommentList(UserPageReq req){
        PageVo<CommentChainVo> page = new PageVo();
        page.setPageNum(req.getPageNum());
        page.setPageSize(req.getPageSize());

        Integer userId = ThreadLocalHolder.getUserId();
        final List<UserCommentPo> pos = userCommentDao.getUserCommentList(userId, req);

        if (pos == null){
            return page;
        }
        List<CommentChainVo> chainVos = buildChainList(pos);

        final Page dbPage = (Page) pos;
        page.setTotal(dbPage.getTotal());
        page.setList(chainVos);
        return page;
    }

    private List<CommentChainVo> buildChainList(List<UserCommentPo> pos) {
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        final List<Integer> replyIds = pos.stream().map(u -> u.getReplyCommentId()).filter(i -> i != null && i > 0).distinct().collect(Collectors.toList());
        Map<Integer,CommentGridVo> replyMap = new ConcurrentHashMap<>();
        if (!CollectionUtils.isEmpty(replyIds)){
            final List<UserCommentPo> replyPos = userCommentDao.getByIds(replyIds);
            if (!CollectionUtils.isEmpty(replyPos)){

                final List<Integer> replyUserIds = replyPos.stream().map(r -> r.getUserId()).distinct().collect(Collectors.toList());

                final Map<Integer, UserBriefVo> userBriefMap = userService.getUserBriefMap(replyUserIds);
                ThreadUtil.detailPool.submit(()->{
                    replyPos.parallelStream().forEach(replyPo->{
                        final CommentGridVo replyGridVo = po2gridVo(replyPo,userBriefMap);
                        replyMap.put(replyPo.getId(),replyGridVo);
                    });
                }).join();
            }
        }

        Set<BusinessDetailService.BusinessReq> businessReqs = new HashSet<>();
        for (UserCommentPo po : pos) {
            BusinessDetailService.BusinessReq br = new BusinessDetailService.BusinessReq(po.getBizId(),po.getBizType());
            businessReqs.add(br);
        }

        final Map<BusinessDetailService.BusinessReq, IBusinessDetailVo> detailMap =
                businessDetailService.getReqDetailMap(businessReqs);

        final List<Integer> userIds = pos.stream().map(p -> p.getUserId()).distinct().collect(Collectors.toList());

        final Map<Integer, UserBriefVo> userBriefMap = userService.getUserBriefMap(userIds);

        List<CommentChainVo> chainVos = pos.parallelStream().map(po -> {
            CommentChainVo chain = new CommentChainVo();
            final CommentGridVo comment = po2gridVo(po, userBriefMap);

            chain.setComment(comment);
            final Integer replyCommentId = po.getReplyCommentId();
            if (replyCommentId != null && replyCommentId > 0) {
                final CommentGridVo commentOn = replyMap.get(replyCommentId);
                chain.setCommentOn(commentOn);
            }
            BusinessDetailService.BusinessReq br = new BusinessDetailService.BusinessReq(po.getBizId(), po.getBizType());

            final IBusinessDetailVo detail = detailMap.get(br);
            chain.setBusinessDetail(detail);
            return chain;
        }).collect(Collectors.toList());
        return chainVos;
    }


    private CommentGridVo po2gridVo(UserCommentPo po, Map<Integer, UserBriefVo> userBriefMap){
        if (po == null){
            return null;
        }
        CommentGridVo gridVo = new CommentGridVo();


        gridVo.setContent(po.getContent());
        gridVo.setCreateTime(po.getCreateTime());
        gridVo.setId(po.getId());

        final Integer userId = po.getUserId();
        UserBriefVo user = null;
        if (userBriefMap==null){
            user = userService.getUserPublicVo(userId);
        }else {
            user = userBriefMap.get(userId);
        }

        if (user == null){
            user = new UserBriefVo();
            user.setId(userId);
            user.setName(user.getName());
            user.setAvatar(user.getAvatar());
        }
        gridVo.setUser(user);
        return gridVo;
    }

    private CommentBizVo po2bizVo(UserCommentPo po, Map<Integer, UserBriefVo> userBriefMap){
        if (po == null){
            return null;
        }
        CommentBizVo bizVo = new CommentBizVo();

        bizVo.setContent(po.getContent());
        bizVo.setCreateTime(po.getCreateTime());
        bizVo.setId(po.getId());
        bizVo.setDataBizId(po.getBizId());
        bizVo.setDataBizType(po.getBizType());

        final Integer userId = po.getUserId();
        UserBriefVo user = null;
        if (userBriefMap==null){
            user = userService.getUserPublicVo(userId);
        }else {
            user = userBriefMap.get(userId);
        }

        if (user == null){
            user = new UserBriefVo();
            user.setId(userId);
            user.setName(user.getName());
            user.setAvatar(user.getAvatar());
        }
        bizVo.setUser(user);
        return bizVo;
    }

    public List<CommentBizVo> getByIds(List<Integer> ids){

        final List<UserCommentPo> pos = userCommentDao.getByIds(ids);
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        final List<Integer> userIds = pos.stream().map(p -> p.getUserId()).distinct().collect(Collectors.toList());
        final Map<Integer, UserBriefVo> userBriefMap = userService.getUserBriefMap(userIds);
        return pos.stream().map(p->po2bizVo(p,userBriefMap)).collect(Collectors.toList());
    }

    private CommentBizVo getBase(Integer id){
        final UserCommentPo load = userCommentDao.load(id);
        return po2bizVo(load,null);
    }

    @Override
    public IBusinessDetailVo getBusinessDetail(String id) {
        final Integer intId = getIntId(id);
        if (intId == null){
            return null;
        }
        return getBase(intId);
    }


}
