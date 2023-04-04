package com.tigerobo.x.pai.biz.auth;

import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.vo.biz.pub.PubBigShotVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.github.GithubUserVo;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.biz.NotifyService;
import com.tigerobo.x.pai.biz.biz.github.GithubUserInfoService;
import com.tigerobo.x.pai.biz.biz.pub.PubBigShotService;
import com.tigerobo.x.pai.biz.biz.pub.PubSiteService;
import com.tigerobo.x.pai.biz.converter.FollowBizConvert;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.auth.dao.FollowDao;
import com.tigerobo.x.pai.dal.auth.entity.FollowPo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FollowService {

    @Autowired
    private FollowDao followDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PubSiteService pubSiteService;
    @Autowired
    private PubBigShotService pubBigShotService;

    @Autowired
    private GithubUserInfoService githubUserInfoService;


    public boolean isFollow( Integer bizId, Integer type){
        Integer userId = ThreadLocalHolder.getUserId();
        return isFollow(bizId, type, userId);
    }

    public boolean isFollow(Integer bizId, Integer type, Integer userId) {
        if (userId == null|| userId <=0||bizId == null||type == null){
            return false;
        }
        List<FollowPo> followList = followDao.getFollowByKey(userId, bizId, type);
        return !CollectionUtils.isEmpty(followList);
    }

    /**
     * @see FollowTypeEnum
     * @param bizIds
     * @param type
     * @return
     */
    public List<Integer> getFollowIds(List<Integer> bizIds, Integer type){
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null||userId<=0){
            return null;
        }
        return getUserBizFollowsByIds(bizIds, type, userId);
    }

    public List<Integer> getUserBizFollowsByIds(List<Integer> bizIds, Integer type, Integer userId) {
        if (CollectionUtils.isEmpty(bizIds)||type == null||userId == null){
            return new ArrayList<>();
        }
        List<FollowPo> followList = followDao.getFollowByBizIdList(userId, bizIds, type);

        if (CollectionUtils.isEmpty(followList)){
            return new ArrayList<>();
        }
        return followList.stream().map(f->f.getBizId()).collect(Collectors.toList());
    }

    public void follow(Integer userId, Integer bizId, Integer type) {

        int vip = subscribe(userId, bizId, type);

        List<FollowPo> followDbs= followDao.getFollowByKey(userId, bizId, type);
        if (followDbs !=null&&followDbs.size()>0) {
            return;
        }
        FollowPo add = new FollowPo();
        add.setBizId(bizId);
        add.setUserId(userId);
        add.setVip(vip);
        add.setBizType(type);
        followDao.add(add);


    }

    private int subscribe(Integer userId, Integer bizId, Integer type) {
        if (!FollowTypeEnum.BIG_SHOT.getType().equals(type)){
            return 0;
        }
        PubBigShotPo subscribe = pubBigShotService.subscribe(userId, bizId);

        if (subscribe==null){
            log.error("bigshot-订阅id:{},不存在",bizId);
            throw new IllegalArgumentException("订阅内容不存在");
        }
        return subscribe.getVip();
    }

    public void cancelFollow(Integer userId, Integer bizId, Integer type) {
        followDao.cancelFollow(userId, bizId, type);
    }


    public int getFollowCountData(Integer userId, Integer bizType){
        if (userId == null){
            return 0;
        }
        return followDao.countByType(userId,bizType);
    }

    public List<Integer> getBizFollowIdList(Integer userId, Integer bizType){

        List<FollowPo> bizFollowList = getBizFollowList(userId, bizType);
        if (CollectionUtils.isEmpty(bizFollowList)){
            return new ArrayList<>();
        }
        return bizFollowList.stream().map(b->b.getBizId()).collect(Collectors.toList());
    }

    public List<FollowPo> getBizFollowList(Integer userId, Integer bizType){
        if (userId == null){
            return null;
        }
        return followDao.getFollowByKey(userId,null,bizType);
    }

    public List<FollowVo> getFollowList(Integer userId,Integer bizType){
//        if (bizType == null){
//            return new ArrayList<>();
//        }
        return getFollowList(userId,Arrays.asList(bizType),null);
    }
    public List<FollowVo> getFollowList(Integer userId,List<Integer> bizTypes){
        return getFollowList(userId,bizTypes,null);
    }
    public List<FollowVo> getFollowList(Integer userId,List<Integer> bizTypes,Integer bizId){
        if (userId == null){
            return new ArrayList<>();
        }

        List<FollowPo> followList = followDao.getFollowByTypes(userId, bizId, bizTypes);

        if (CollectionUtils.isEmpty(followList)){
            return new ArrayList<>();
        }

        Integer loginUserId = ThreadLocalHolder.getUserId();

        boolean sameUser = userId.equals(loginUserId);
        Map<Integer, User> userMap = new HashMap<>();
        Map<Integer,PubSiteVo> siteMap = new HashMap<>();
        Map<Integer,PubBigShotVo> bigShotVoMap = new HashMap<>();

        Map<Integer, GithubUserDto> githubUserMap = new HashMap<>();
        initFollowMap(followList, userMap, siteMap, bigShotVoMap,githubUserMap);

        List<FollowVo> followVos = buildFollowList(followList, userMap, siteMap, bigShotVoMap,sameUser,githubUserMap);
        return followVos;
    }

    private List<FollowVo> buildFollowList(List<FollowPo> followList, Map<Integer, User> userMap,
                                           Map<Integer, PubSiteVo> siteMap, Map<Integer, PubBigShotVo> bigShotVoMap,
                                           boolean sameUser, Map<Integer, GithubUserDto> githubUserMap) {
        List<FollowVo> followVos = new ArrayList<>();
        for (FollowPo followPo : followList) {
            FollowVo followVo = null;
            Integer bizId = followPo.getBizId();
            if (FollowTypeEnum.USER.getType().equals(followPo.getBizType())){
                User user = userMap.get(bizId);
                followVo = FollowBizConvert.convert(user);
            }else if(FollowTypeEnum.SITE.getType().equals(followPo.getBizType())){
                followVo = FollowBizConvert.convert(siteMap.get(bizId));
            }else if(FollowTypeEnum.BIG_SHOT.getType().equals(followPo.getBizType())){
                followVo = FollowBizConvert.convert(bigShotVoMap.get(bizId));
            }else if(FollowTypeEnum.GITHUB_USER.getType().equals(followPo.getBizType())){
                followVo = FollowBizConvert.convert(githubUserMap.get(bizId));
            }
            if (followVo!=null){
                followVo.setFollow(true);
                if (sameUser){
                    followVo.setRole(Role.OWNER.toString());
                }else {
                    followVo.setRole(Role.GUEST.toString());
                }
                followVo.setCreateTime(followPo.getCreateTime());
                followVos.add(followVo);
            }
        }
        return followVos;
    }

    private void initFollowMap(List<FollowPo> followList, Map<Integer, User> userMap, Map<Integer, PubSiteVo> siteMap,
                               Map<Integer, PubBigShotVo> bigShotVoMap, Map<Integer, GithubUserDto> githubUserMap) {
        List<Integer> userIds = new ArrayList<>();
        List<Integer> siteIds = new ArrayList<>();
        List<Integer> bigShotIds = new ArrayList<>();
        List<Integer> githubUserIds = new ArrayList<>();

        for (FollowPo followPo : followList) {
            Integer bizId = followPo.getBizId();
            Integer bizType = followPo.getBizType();

            FollowTypeEnum followTypeEnum = FollowTypeEnum.getByType(bizType);

            if (FollowTypeEnum.USER== followTypeEnum){
                userIds.add(bizId);
            }else if (FollowTypeEnum.SITE == followTypeEnum){
                siteIds.add(bizId);
            }else if (FollowTypeEnum.BIG_SHOT == followTypeEnum){
                bigShotIds.add(bizId);
            }else if (FollowTypeEnum.GITHUB_USER == followTypeEnum){
                githubUserIds.add(bizId);
            }

        }


        List<User> userList = userService.getByIds(userIds);
        if (!CollectionUtils.isEmpty(userList)){
            for (User user : userList) {
                userMap.put(user.getId(),user);
            }
        }


        List<PubSiteVo> siteList = pubSiteService.getByIds(siteIds);
        if (!CollectionUtils.isEmpty(siteList)){
            for (PubSiteVo vo : siteList) {
                siteMap.put(vo.getId(), vo);
            }
        }

        List<PubBigShotVo> bigShotVos = pubBigShotService.getByIds(bigShotIds);


        if (!CollectionUtils.isEmpty(bigShotVos)){
            for (PubBigShotVo bigShotVo : bigShotVos) {
                bigShotVoMap.put(bigShotVo.getId(),bigShotVo);
            }
        }

        if (githubUserIds.size()>0){
            for (Integer githubUserId : githubUserIds) {
                final GithubUserDto githubUserDto = githubUserInfoService.loadByUserId(githubUserId);
                if (githubUserDto!=null){
                    githubUserMap.put(githubUserId,githubUserDto);
                }
            }

        }
    }


    public List<Integer> getFollowUserIdsByUserId(Integer userId){

        final List<FollowPo> followByBizIdList = followDao.getFollowByBizIdList(userId, null, FollowTypeEnum.USER.getType());
        if (CollectionUtils.isEmpty(followByBizIdList)){
            return null;
        }

        return followByBizIdList.stream().map(FollowPo::getBizId).distinct().collect(Collectors.toList());
    }
}
