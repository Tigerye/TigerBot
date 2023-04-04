package com.tigerobo.x.pai.biz.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.dto.UserCommitSiteDto;
import com.tigerobo.x.pai.api.enums.NotifyMessageTypeEnum;
import com.tigerobo.x.pai.api.enums.NotifyTypeEnum;
import com.tigerobo.x.pai.api.req.UserNotifyPageReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.notify.NotifyCountVo;
import com.tigerobo.x.pai.api.vo.notify.NotifyJumpVo;
import com.tigerobo.x.pai.api.vo.notify.NotifyVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.converter.NotifyConvert;
import com.tigerobo.x.pai.biz.user.comment.UserCommentService;
import com.tigerobo.x.pai.biz.user.fans.FanService;
import com.tigerobo.x.pai.biz.user.thumb.UserThumbService;
import com.tigerobo.x.pai.biz.utils.JacksonUtil;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.user.UserNotifyDao;
import com.tigerobo.x.pai.dal.biz.entity.user.UserNotifyPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.tigerobo.x.pai.api.enums.NotifyTypeEnum.*;

@Slf4j
@Service
public class NotifyService {

    @Autowired
    private UserNotifyDao userNotifyDao;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private UserCommentService userCommentService;

    @Autowired
    private UserThumbService userThumbService;

    @Autowired
    private FanService fanService;

    public void addNotify(UserNotifyPo po) {
        if (po.getMessageEntity() == null) {
            po.setMessageEntity("");
        }
        userNotifyDao.add(po);
    }

    public List<NotifyCountVo> countUnReadGroupByType() {

        return Arrays.stream(NotifyTypeEnum.values()).parallel().map(e->{
            int typeCount = countUnRead(e);
            final NotifyCountVo v = new NotifyCountVo(e.getType(), e.getText(), typeCount);
            return v;
        }).filter(v->v!=null).collect(Collectors.toList());
    }

    public int countUnRead() {
        int count = 0;
        for (NotifyTypeEnum value : NotifyTypeEnum.values()) {
            int typeCount = countUnRead(value);
            count += typeCount;
        }
        return count;
    }

    public int countUnRead(NotifyTypeEnum notifyEnum) {
        Integer notifyType = notifyEnum.getType();
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null || userId <= 0) {
            return 0;
        }

        Long preSecond = redisCacheService.getLong(getUserViewNotifyKey(notifyType, userId));
        long nowTime = System.currentTimeMillis();
        long checkPreTime = nowTime / 1000L - 3 * 30 * 24 * 3600;
        if (preSecond == null || preSecond < checkPreTime) {
            preSecond = checkPreTime;
        }
        final Date preTime = new Date(preSecond * 1000L);
        if (notifyEnum == COMMIT_MEDIA||notifyEnum== SYSTEM){
            int count = userNotifyDao.countUnRead(preTime, userId,notifyType);
            return count;
        }else if (notifyEnum == COMMENT){
            return userCommentService.countWaitReadComment(userId,preTime);
        }else if (notifyEnum == THUMB){
            return userThumbService.countWaitRead(userId,preTime);
        }
//        else if (notifyEnum == FOLLOW){
//            return fanService.countWaitReadComment(userId,preTime);
//        }
        return 0;
    }

    public PageVo<NotifyVo> getNotifyPage(UserNotifyPageReq reqVo) {

        Validate.isTrue(reqVo.getNotifyType() != null, "未选择类型");
        PageVo<NotifyVo> pageVo = new PageVo<>();
        pageVo.setPageSize(reqVo.getPageSize());
        pageVo.setPageNum(reqVo.getPageNum());
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null) {
            return pageVo;
        }

        Page<UserNotifyPo> userNotifyPage = userNotifyDao.getUserNotifyPage(reqVo, reqVo.getNotifyType(), userId);

        pageVo.setTotal(userNotifyPage.getTotal());
        List<NotifyVo> vos = convert2Vo(userNotifyPage);
        pageVo.setList(vos);

        setReadTime(reqVo.getNotifyType(), userId);
        return pageVo;
    }

    public void setCommentReadTime(Integer userId){
        setReadTime(COMMENT.getType(),userId);
    }

    @Deprecated
    public void setFanReadTime(Integer userId){
//        setReadTime(FOLLOW.getType(),userId);
    }

    public void setThumbReadTime(Integer userId){
        setReadTime(THUMB.getType(),userId);
    }
    private void setReadTime(Integer notifyType, Integer userId) {
        long nowTime = System.currentTimeMillis();
        redisCacheService.set(getUserViewNotifyKey(notifyType, userId), String.valueOf(nowTime / 1000));
    }

    private List<NotifyVo> convert2Vo(List<UserNotifyPo> poList) {
        if (CollectionUtils.isEmpty(poList)) {
            return new ArrayList<>();
        }
        List<NotifyVo> vos = new ArrayList<>();


        for (UserNotifyPo po : poList) {
            NotifyVo vo = initVo(po);
            vos.add(vo);
        }
        return vos;
    }

    private NotifyVo initVo(UserNotifyPo po) {
        NotifyVo vo = NotifyConvert.po2vo(po);

        Integer notifyType = vo.getNotifyType();
        NotifyTypeEnum typeEnum = NotifyTypeEnum.getByType(notifyType);
        if (typeEnum != null) {
            vo.setNotifyTypeName(typeEnum.getText());
        }
        Integer messageType = po.getMessageType();
        NotifyMessageTypeEnum messageTypeEnum = NotifyMessageTypeEnum.getByType(messageType);
        if (StringUtils.isNotBlank(po.getMessageEntity()) && messageTypeEnum != null) {
            Object messageEntityObj = null;
            switch (messageTypeEnum) {
                case COMMIT_FAIL:
                case COMMIT_SUCCESS:
                    messageEntityObj = JacksonUtil.json2Bean(po.getMessageEntity(), UserCommitSiteDto.class);
                    break;
                default:
                    messageEntityObj = po.getMessageEntity();
                    break;
            }
            vo.setMessageEntity(messageEntityObj);
        }

        String jump = po.getJump();
        if (StringUtils.isNotBlank(jump)) {
            List<NotifyJumpVo> jumpVos = JSONArray.parseArray(jump, NotifyJumpVo.class);
            vo.setJumpList(jumpVos);
        }else {
            vo.setJumpList(new ArrayList<>());
        }
        return vo;
    }

    private String getUserViewNotifyKey(Integer notifyType, Integer userId) {
        return "user:notify:view:time:" + userId + ":" + notifyType;
    }

}
