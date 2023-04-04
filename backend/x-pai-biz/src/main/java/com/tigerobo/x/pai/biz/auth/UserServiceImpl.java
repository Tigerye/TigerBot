package com.tigerobo.x.pai.biz.auth;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.auth.vo.UserVo;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.converter.UserConvert;
import com.tigerobo.x.pai.biz.utils.Md5Util;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.WechatHandler;
import com.tigerobo.x.pai.dal.auth.dao.GroupDao;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.auth.mapper.UserMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.tigerobo.x.pai.api.auth.entity.Role.GUEST;
import static com.tigerobo.x.pai.api.auth.entity.Role.OWNER;

@Slf4j
@Data
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WechatHandler wechatHandler;


    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

//    Cache<Integer, User> userCache = Caffeine.newBuilder()
//            .expireAfterWrite(3, TimeUnit.MINUTES)
//            .maximumSize(300)
//            .build();


    @Autowired
    private RedisCacheService redisCacheService;


    @Override
    public List<User> getBaseUserByIds(List<Integer> userIds){

        if (CollectionUtils.isEmpty(userIds)){
            return null;
        }
        List<UserDo> userDoList = userDao.getByIds(userIds);

        if (CollectionUtils.isEmpty(userDoList)){
            return null;
        }

        return userDoList.stream().map(po -> UserConvert.memberPo2dto(po)).collect(Collectors.toList());
    }

    @Override
    public Integer getIdByUuidCache(String uuid){
        if (StringUtils.isBlank(uuid)){
            return null;
        }

        String key = "pai:user:uuid:rel:"+uuid;

        int userId = redisCacheService.getNum(key);
        if (userId>0){
            return userId;
        }
        Integer id = getIdByUuId(uuid);

        if (id!=null&&id>0){
            redisCacheService.set(key,String.valueOf(id),24*3600);

        }
        return id;
    }

    @Override
    public boolean userIdExist(Integer id){
        if (id == null||id ==0){
            return false;
        }

        String key = "pai:user:exist:id:"+id;

        boolean exist = redisCacheService.exist(key);
        if (exist){
            return true;
        }

        UserDo user = userDao.load(id);

        if (user!=null){
            redisCacheService.set(key,String.valueOf(id),4*3600);
        }
        return true;
    }


    @Override
    public List<User> getGroupMemberList(String groupUuid) {
        if (groupUuid == null) {
            return null;
        }
        List<UserDo> groupUsers = userDao.getGroupUsers(groupUuid);
        List<User> users = new ArrayList<>();
        if (!CollectionUtils.isEmpty(groupUsers)) {
            users = groupUsers.stream().map(UserConvert::memberPo2dto).collect(Collectors.toList());
        }
        return users;
    }


    @Override
    public User getFromCache(Integer userId) {
        if (userId == null) {
            return null;
        }
        String cacheKey = "pai:user:info:"+userId;

        final String s = redisCacheService.get(cacheKey);
        if (StringUtils.isNotBlank(s)){
            try {
                return JSON.parseObject(s, User.class);
            }catch (Exception ex){
                log.error("user get from redis",ex);
            }
        }
        final User user = getById(userId);
        if (user == null){
            return null;
        }
        redisCacheService.set(cacheKey,JSON.toJSONString(user),3*60);
        return user;
    }

    @Override
    public User getById(Integer userId) {
        UserDo userDo = userDao.getById(userId);

        return UserConvert.po2dto(userDo);
    }


    @Override
    public List<User> getByIds(List<Integer> userIds){
        if (CollectionUtils.isEmpty(userIds)){
            return null;
        }

        List<UserDo> userDos = userDao.getByIds(userIds);

        if (CollectionUtils.isEmpty(userDos)){
            return null;
        }
        Integer userId = ThreadLocalHolder.getUserId();
        return userDos.stream().map(po->{
            User user = UserConvert.memberPo2dto(po);

            if (userId!=null&&userId.equals(user.getId())){
                user.setRole(OWNER);
            }else {
                user.setRole(OWNER);
            }
            return user;
        }).collect(Collectors.toList());

    }
    @Override
    public User getUserByOther(Integer userId) {
        if (userId == null||userId<=0){
            return null;
        }
        UserDo userDo = userDao.getById(userId);

        return UserConvert.memberPo2dto(userDo);
    }

    public Map<Integer,UserBriefVo> getUserBriefMap(Collection<Integer> userIds){
        Map<Integer,UserBriefVo> map = new HashMap<>();
        if (CollectionUtils.isEmpty(userIds)){
            return map;
        }

        final List<UserBriefVo> collect = userIds.parallelStream().map(id -> getUserPublicVo(id))
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)){
            return map;
        }
        for (UserBriefVo userBriefVo : collect) {
            map.put(userBriefVo.getId(),userBriefVo);
        }
        return map;
    }

    public Map<Integer,User> getUserMap(Collection<Integer> userIds){
        Map<Integer,User> map = new HashMap<>();
        if (CollectionUtils.isEmpty(userIds)){
            return map;
        }

        final List<User> collect = userIds.parallelStream().map(id -> getCacheUserByOther(id)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)){
            return map;
        }
        for (User userBriefVo : collect) {
            map.put(userBriefVo.getId(),userBriefVo);
        }
        return map;
    }

    public UserBriefVo getUserPublicVo(Integer userId){
        if (userId == null||userId<=0){
            return null;
        }
        User cacheUser = getFromCache(userId);

        if (cacheUser == null){
            return null;
        }
        UserBriefVo vo = UserConvert.user2publicVo(cacheUser);

        Integer loginUserId = ThreadLocalHolder.getUserId();

        if (loginUserId!=null&&loginUserId.equals(cacheUser.getId())){
            vo.setRole(OWNER);
        }else {
            vo.setRole(GUEST);
        }
        return vo;

    }

    @Override
    public User getCacheUserByOther(Integer userId) {
        if (userId == null||userId<=0){
            return null;
        }
        User cacheUser = getFromCache(userId);
        if (cacheUser == null){
            return null;
        }
        Integer loginUserId = ThreadLocalHolder.getUserId();

        User user = UserConvert.member2dto(cacheUser);

        if (loginUserId!=null&&loginUserId.equals(cacheUser.getId())){
            user.setRole(OWNER);
        }else {
            user.setRole(GUEST);
        }
        return user;

    }

    @Override
    public Integer getIdByUuId(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            return null;
        }
        UserDo userdo = userDao.getByUuid(uuid);
        return userdo == null ? null : userdo.getId();
    }


    @Override
    public Integer update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("参数为空");
        }
        Integer userId = ThreadLocalHolder.getUserId();

        UserDo tmp = userDao.getById(userId);
        if (tmp == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }

        UserDo userDo = UserConvert.dto2po(user);
        userDo.setId(tmp.getId());
        userDo.setUuid(tmp.getUuid());
//        userDo.setUpdateBy(String.valueOf(userId));
        userDo.setAccount(null);
        userDo.setWechat(null);
        // 修改密码
        userDo.setPassword(!StringUtils.isEmpty(user.getPassword()) ? Md5Util.getPassword(user.getPassword()) : userDo.getPassword());
//        userDo.setCurrGroupId(tmp.getCurrGroup().getId());
//        userDo.setCurrGroupUuid(tmp.getCurrGroup().getUuid());
        try {
            return userDao.update(userDo);
//            return this.userMapper.updateByPrimaryKeySelective(userDo);
        } catch (Exception e) {
            log.error("failed to modify user: {}", user);
            log.error("", e);
            throw e;
        } finally {
            user.setId(userDo.getId());
            user.setUuid(userDo.getUuid());
        }
    }

    @Override
    public UserVo getUserVo(Integer id) {
        UserDo userDo = this.userDao.getById(id);
        if (userDo == null) {
            return null;
        }
        User user = UserConvert.po2dto(userDo);
        return UserVo.builder().user(user).personalGroup(new Group()).role(OWNER).build();
    }


}
