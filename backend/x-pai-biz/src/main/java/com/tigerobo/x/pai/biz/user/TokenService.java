package com.tigerobo.x.pai.biz.user;

import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.constant.RedisConstants;
import com.tigerobo.x.pai.biz.utils.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TokenService {
    private static final int token_expire_time = 3 * 30 * 24 * 3600;

    private static final String SPLIT = "-";

    @Value("#{${pai.token.user.map}}")
    Map<String, Integer> tokenUserMap = new HashMap<>();


    @Autowired
    private RedisCacheService redisCacheService;

    public String produceToken(Integer uid) {
        String token = createToken(uid);
        String userKey = String.format(RedisConstants.UC_UID_TOKEN, uid);
        redisCacheService.set(userKey, token, token_expire_time);
        return token;
    }



    private String createToken(int uid){
        long expTime = (System.currentTimeMillis() / 1000)+token_expire_time;
        String text = uid+SPLIT+expTime;
        return EncryptionUtil.encrypt4NetAndJava(text);
    }

    public Integer getUserIdByToken(String token){

        if (StringUtils.isNotBlank(token)&& !CollectionUtils.isEmpty(tokenUserMap)){
            final Integer userId = tokenUserMap.get(token);
            if (userId!=null&&userId>0){
                return userId;
            }
        }

        String text = decodeToken(token);
        if (StringUtils.isBlank(text)){
            return null;
        }
        String[] split = text.split(SPLIT);

        if (split.length!=2){
            return null;
        }
        String uid = split[0];
        String time = split[1];
        if (!uid.matches("\\d+")){
            return null;
        }
        if (!time.matches("\\d+")){
            return null;
        }
        if (Long.parseLong(time)*1000<System.currentTimeMillis()){
            return null;
        }

        return Integer.parseInt(uid);
        //现在不护踢
//        String userKey = String.format(RedisConstants.UC_UID_TOKEN, uid);
//        String existToken = redisCacheService.get(userKey);
//        if (token.equals(existToken)){
//            return Integer.parseInt(uid);
//        }
//        return null;
    }

    private String decodeToken(String token) {
        return StringUtils.isBlank(token) ? "" : EncryptionUtil.decrypt4NetAndJava(token);
    }


}
