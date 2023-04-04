package com.tigerobo.x.pai.biz.message.service;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.uc.dto.BaseMobileDto;
import com.tigerobo.x.pai.api.uc.dto.ConfirmCodeDto;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.constant.RedisConstants;
import com.tigerobo.x.pai.biz.message.MessageSender;
import com.tigerobo.x.pai.biz.utils.MobileUtil;
import com.tigerobo.x.pai.biz.utils.RandomUtils;
import com.tigerobo.x.pai.biz.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author:Wsen
 * @time: 2020/4/23
 **/
@Slf4j
@Service
public class ConfirmCodeService {

    private static final int codeType = 1;
    @Autowired
    private RedisCacheService redisCacheService;

//    @Autowired
//    private CaptchaService captchaService;

    @Value("${pai.user.special.code}")
    private String specialCode;

    @Value("${pai.sms.channel:}")
    private String smsChannel;



    @Value("${pai.sms.openDefaultChannel:true}")
    private boolean openDefaultChannel;

    public void checkCode(ConfirmCodeDto codeDto) {
        Preconditions.checkArgument(codeDto != null, "参数为空");
        Preconditions.checkArgument(StringUtils.isNotEmpty(codeDto.getMobile()), "账号不能为空");
        Preconditions.checkArgument(StringUtils.isNotEmpty(codeDto.getCode()), "验证码不能为空");
        BaseMobileDto baseMobile = MobileUtil.getBaseMobile(codeDto.getArea(),codeDto.getMobile());
        checkConfirmCode(baseMobile,codeDto.getCode());
    }

    private void verifyConfirmCode(String mobile, int codeType, String confirmCode) {
        String codeKey = RedisKeyUtil.getSmsConfirmCodeRedisKey( mobile, codeType);
        String validateValue = redisCacheService.get(codeKey);
        Preconditions.checkArgument(confirmCode.equalsIgnoreCase(validateValue), "验证码不正确");
    }

    /***
     * 发送短信验证码
     */
    public String sendLoginConfirmCode(ConfirmCodeDto confirmCodeDto) {
          String oriMobile = confirmCodeDto.getMobile();
        BaseMobileDto baseMobile = MobileUtil.getBaseMobile(confirmCodeDto.getArea(),oriMobile);
        Preconditions.checkArgument(baseMobile != null, "参数错误");
        String areaCode = baseMobile.getAreaCode();
        String mobile = baseMobile.getMobile();

        String fullMobile = baseMobile.getMobile();

//        checkBlack(mobile);
        checkSpeed( fullMobile);
        int cnt = checkSendCnt( fullMobile);


//        String validateValue = redisCacheService.get(codeKey);
        int validateCode = RandomUtils.getRandomInt(4);
        String validateValue = String.valueOf(validateCode);

        String codeKey = RedisKeyUtil.getSmsConfirmCodeRedisKey( fullMobile, codeType);
        String setResult = redisCacheService.set(codeKey, validateValue, 600);
        Preconditions.checkArgument(setResult != null, "验证码设置缓存失败");

        String speedKey = MessageFormat.format(RedisConstants.SMS_SPEED, fullMobile);
        redisCacheService.set(speedKey, "", 15);

        if (cnt>=2&& openDefaultChannel){
            smsChannel = "aliyun";
        }

        MessageSender.sendCode(areaCode, fullMobile, validateValue,smsChannel);
        log.info("mobile:{},confirmCod:{}",mobile , validateValue);
        String cntKey = RedisKeyUtil.getCntKey( fullMobile);
        redisCacheService.incr(cntKey);
        if (cnt == 0) {
            redisCacheService.expire(cntKey, 18 * 60 * 60);
        }
        return validateValue;
    }


    private void checkSpeed(String mobile) {
        String key = MessageFormat.format(RedisConstants.SMS_SPEED, mobile);
        Preconditions.checkArgument(!redisCacheService.exist(key), "发送验证码过于频繁，请稍后30秒后再次发送");
    }

    private int checkSendCnt(String mobile) {
        String key = RedisKeyUtil.getCntKey( mobile);
        String count = redisCacheService.get(key);
        int cnt = 0;
        if (count != null && StringUtils.isNumeric(count)) {
            cnt = Integer.parseInt(count);
            int cntMax = 15;
            Preconditions.checkArgument(cnt < cntMax, "发送次数过多，请等待1小时后操作");
        }
        return cnt;
    }

    public void checkConfirmCode(BaseMobileDto mobile,  String confirmCode) {
        String specialConfirmCode = getSpecialConfirmCode();
        if (!StringUtils.isEmpty(specialConfirmCode)&&specialConfirmCode.equals(confirmCode)){
            return;
        }
        String fullMobile = mobile.getMobile();
        String codeKey = RedisKeyUtil.getSmsConfirmCodeRedisKey( fullMobile, codeType);
        String validateValue = redisCacheService.get(codeKey);

        Preconditions.checkArgument(confirmCode.equalsIgnoreCase(validateValue), ResultCode.CONFIRM_CODE_ERROR.getMsg());
    }

    public void deleteConfirmCodeKey(BaseMobileDto mobile) {
        String codeKey = RedisKeyUtil.getSmsConfirmCodeRedisKey( mobile.getMobile(), codeType);
        redisCacheService.del(codeKey);
    }

    private String getSpecialConfirmCode(){
//        return "0726";
        return specialCode;
    }


}
