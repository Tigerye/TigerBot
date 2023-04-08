package com.tigerbot.chat.service;

import com.alibaba.fastjson.JSON;
import com.tigerbot.chat.ai.dto.GptSession;
import com.tigerbot.chat.model.ChatContext;
import com.tigerbot.chat.service.cache.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MultiSessionService {

    @Value("${pai.qaGpt.reqSessionSize:3}")
    int reqSessionSize;
    @Value("${api.qaGpt.multiExpireTime:360}")
    int multiExpireTime;


    @Autowired
    private RedisCacheService redisCacheService;


    public List<GptSession> getSessionList(String userSessionId) {
        if (StringUtils.isBlank(userSessionId)) {
            return new ArrayList<>();
        }

        int limit = reqSessionSize;

        if (limit < 1) {
            limit = 1;
        }

        String key = getMultiRedisKey(userSessionId);
        final List<String> lrange = redisCacheService.lrange(key, 0, limit - 1);
        if (lrange != null && lrange.size() > 1) {
            Collections.reverse(lrange);
        }
        List<GptSession> sessions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(lrange)) {

            for (String s : lrange) {
                final GptSession gptSession = JSON.parseObject(s, GptSession.class);
                sessions.add(gptSession);
            }
            if (lrange.size() >= limit) {
                redisCacheService.ltrim(key, 0, limit - 1);
            }
        }

        return sessions;
    }



    public void multiChatCache(ChatContext context, String result) {
        if (StringUtils.isBlank(result)) {
            return;
        }
        //保持多轮会话
        if (StringUtils.isNotBlank(context.getUserSession())) {
            GptSession gptSession = new GptSession();
            gptSession.setHuman(context.getText());
            gptSession.setAssistant(result);
            String multiKey = getMultiRedisKey(context.getUserSession());
            redisCacheService.lpush(multiKey, JSON.toJSONString(gptSession));
            redisCacheService.expire(multiKey, multiExpireTime);
        }

    }


    private static String multiSessionKey = "pai:gpt:session:";
    private String getMultiRedisKey(String userSession) {
        return multiSessionKey + userSession;
    }


}
