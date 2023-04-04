package com.tigerobo.x.pai.biz.biz.customer;

import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.biz.ai.AiDailyLimitService;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.user.BlackUserService;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class ModelConsumeCheckService {

    @Autowired
    private AiArtImageDao aiArtImageDao;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @Autowired
    private BlackUserService blackUserService;

    @Autowired
    private AiDailyLimitService aiDailyLimitService;

    @Value("${pai.ai.artImage.limit:20}")
    int artImageLimit;
    public void checkArtImgCall(Integer userId){

        final int count = countUserArtImgRemainCall(userId);
        if (count<=0){
            throw new IllegalArgumentException("今日次数已用完");
        }
    }

    public int countUserTotal(Integer userId){
        if (userId == null||userId<=0){
            return 0;
        }
        if (roleService.isAdmin(userId)){
            return 99999;
        }
        if (blackUserService.isBlackUser(userId)){
            return 0;
        }
        final Integer userDailyLimit = aiDailyLimitService.getUserDailyLimit(userId);
        if (userDailyLimit!=null){
            return userDailyLimit;
        }
        return artImageLimit;
    }

    public int countUsed(Integer userId){
        if (userId == null||userId<=0){
            return 0;
        }

        final Date truncate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        final int count = aiArtImageDao.countUserDayUse(userId, truncate);
        return count;
    }
    public int countUserArtImgRemainCall(Integer userId){
        final int total = countUserTotal(userId);
        final int used = countUsed(userId);
        return Math.max(0,total-used);
    }
}
