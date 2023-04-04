package com.tigerobo.x.pai.biz.admin;

import com.tigerobo.x.pai.api.admin.auth.AdminAuth;
import com.tigerobo.x.pai.api.admin.req.AdminLoginReq;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.Md5Util;
import com.tigerobo.x.pai.dal.admin.dao.SsoUserDao;
import com.tigerobo.x.pai.dal.admin.entity.SsoUserPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class SsoUserService {

    @Autowired
    private SsoUserDao ssoUserDao;

    @Autowired
    private RedisCacheService redisCacheService;

    int expireTime = 3*30*24*3600;


    public Integer addUser(String userName,String password){

        if (org.apache.commons.lang3.StringUtils.isAnyBlank(userName,password)){
            throw new IllegalArgumentException("参数不正确");
        }
        SsoUserPo userPo = ssoUserDao.getByUserName(userName);

        String saltPassword = saltPassword(password);

        Integer userId;
        if (userPo!=null){
            SsoUserPo update = new SsoUserPo();
            update.setId(userPo.getId());
            update.setPassword(saltPassword);
            ssoUserDao.update(update);
            userId = userPo.getId();
        }else {
            SsoUserPo add = new SsoUserPo();
            add.setUserName(userName);
            add.setPassword(saltPassword);
            ssoUserDao.add(add);
            userId = add.getId();
        }
        return userId;
    }

    private String saltPassword(String oriPassword){
        return Md5Util.getMd5(oriPassword);
    }

    public AdminAuth login(AdminLoginReq req){
        Validate.isTrue(req!=null,"参数为空");
        Validate.isTrue(!StringUtils.isEmpty(req.getUserName()),"账号或密码不正确");
        Validate.isTrue(!StringUtils.isEmpty(req.getPassword()),"账号或密码不正确");

        SsoUserPo po = ssoUserDao.getByUserName(req.getUserName());
        Validate.isTrue(po!=null,"账号或密码不正确");
        Validate.isTrue(saltPassword(req.getPassword()).equals(po.getPassword()),"账号或密码不正确");
        //Validate.isTrue(req.getPassword().equals(po.getPassword()),"账号或密码不正确");

        final Integer userId = po.getId();
        String token = getToken(userId);
        addToken(userId,token);
//
//        String tokenCacheKey = getTokenKey(userId);
//        redisCacheService.set(tokenCacheKey,token,expireTime);

        AdminAuth auth = new AdminAuth();
        auth.setToken(token);
        return auth;

    }

    private void addToken(Integer userId,String token){

        String key = "admin:sso:"+userId;
        final Long lpush = redisCacheService.lpush(key, token);

        if (lpush!=null&&lpush>5){
            redisCacheService.ltrim(key,0,4);
        }
        redisCacheService.expire(key,30*24*3600);
    }

    private boolean existToken(Integer userId,String token){
        String key = "admin:sso:"+userId;
        final List<String> lrange = redisCacheService.lrange(key, 0, 4);
        if (CollectionUtils.isEmpty(lrange)){
            return false;
        }
        return lrange.contains(token);
    }
    public SsoUserPo getByToken(String token){

        Integer userId = getTokenUserId(token);
        if (userId == null){
            return null;
        }

        if (!existToken(userId,token)){
            return null;
        }
        return ssoUserDao.load(userId);
    }

    private Integer getTokenUserId(String token){
        if (StringUtils.isEmpty(token)){
            return null;
        }
        String[] arr = token.split("_");
        if (arr.length<2){
            return null;
        }

        String userIdStr = arr[1];
        if (!userIdStr.matches("\\d+")){
            return null;
        }
        return Integer.parseInt(userIdStr);

    }
    private String getToken(int id){
        return IdGenerator.getId()+"_"+id;
    }

    private String getTokenKey(Integer userId){
        return "pai:admin:u:t:"+userId;
    }

    public Boolean checkToken(String token){
        if(!StringUtils.isEmpty(token)){
            Integer userId = getTokenUserId(token);
            if (userId == null){
                return false;
            }
            String tokenKey = getTokenKey(userId);
            String cacheToken = redisCacheService.get(tokenKey);
            if (token.equalsIgnoreCase(cacheToken)){
                return true;
            }
        }

        return false;
    }
}
