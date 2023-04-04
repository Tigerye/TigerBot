package com.tigerobo.x.pai.biz.user.fans;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import com.tigerobo.x.pai.api.vo.user.UserIdPageReq;
import com.tigerobo.x.pai.api.vo.user.fan.FanVo;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.auth.UserServiceImpl;
import com.tigerobo.x.pai.biz.biz.NotifyService;
import com.tigerobo.x.pai.biz.converter.FollowBizConvert;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.auth.dao.FollowDao;
import com.tigerobo.x.pai.dal.auth.entity.FollowPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FanService {

    @Autowired
    private FollowDao followDao;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private NotifyService notifyService;

    @Autowired
    private FollowService followService;
    public int countUserFans(Integer userId) {


        if (userId == null || userId <= 0) {
            return 0;
        }
        PageReqVo reqVo = new PageReqVo();
        reqVo.setPageSize(1);
        final List<FollowPo> userFans = followDao.getUserFans(userId, reqVo);

        if (userFans == null) {
            return 0;
        }

        Page page = (Page) userFans;

        return (int) page.getTotal();
    }
    public PageVo<FollowVo> getMyFansPage(UserIdPageReq req) {
        final Integer userId = ThreadLocalHolder.getUserId();
        req.setUserId(userId);
        return getUserFansPage(req);
    }


    public PageVo<FollowVo> getUserFansPage(UserIdPageReq req) {
        PageVo<FollowVo> pageVo = new PageVo(req);
        Integer userId = req.getUserId();
        if (userId == null || userId <= 0) {
            return pageVo;
        }
        final List<FollowPo> userFans = followDao.getUserFans(userId, req);
        if (userFans == null) {
            return pageVo;
        }

        notifyService.setFanReadTime(userId);

        final List<Integer> userIds = userFans.stream().map(u -> u.getUserId()).distinct().collect(Collectors.toList());

        final Integer loginUserId = ThreadLocalHolder.getUserId();

        final List<Integer> followIds = followService.getUserBizFollowsByIds(userIds, 0, loginUserId);

        final Map<Integer, User> userBriefMap = userService.getUserMap(userIds);

        List<FollowVo> vos = new ArrayList<>();
        for (FollowPo po : userFans) {
            final Integer fanUserId = po.getUserId();
            final User userBriefVo = userBriefMap.get(fanUserId);
            final FollowVo convert = FollowBizConvert.convert(userBriefVo);
            if (convert == null){
                continue;
            }
            convert.setCreateTime(po.getCreateTime());
            if (followIds.contains(fanUserId)){
                convert.setFollow(true);
            }
            vos.add(convert);
        }


        pageVo.setList(vos);

        Page page = (Page)userFans;

        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    public int countWaitReadComment(Integer userId, Date preTime){
        if (userId == null||userId<=0){
            return 0;
        }
        return followDao.countFanUnRead(userId,preTime);
    }
}
