package com.tigerbot.chat.ai.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerbot.chat.ai.dto.GptSession;
import com.tigerbot.chat.model.ChatContext;
import com.tigerbot.chat.service.MultiSessionService;
import com.tigerbot.chat.util.HttpReqUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GptModelService {

    @Value("${pai.gpt.replaceWords:Belle}")
    private String replaceWords;
    @Value("${pai.gpt.replaceTarget:TigerBot}")
    private String replaceTarget;

    @Value("${pai.gpt.urls}")
    List<String> gptUrlList;

    public void setGptUrlList(String text){
        if (StringUtils.isBlank(text)){
            return;
        }
        gptUrlList = Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    AtomicInteger callCount = new AtomicInteger(0);
    @Autowired
    private MultiSessionService multiSessionService;


    public String chatReq(ChatContext context){

        final List<GptSession> sessionList = multiSessionService.getSessionList(context.getUserSession());
        String result = doChatReq(context, sessionList);
        if (StringUtils.isBlank(result)){
            return null;
        }
        multiSessionService.multiChatCache(context, result);
        return result;
    }
    public String doChatReq(ChatContext context, List<GptSession> sessions) {
        final String url = getUrl();
        Validate.isTrue(StringUtils.isNotBlank(url), "未配置url");
        context.setUrl(url);

        Map<String, Object> reqData = new HashMap<>();
        String text = context.getText();
        reqData.put("query", text);
        if (context.isUseMulti()&&!CollectionUtils.isEmpty(sessions)) {
            reqData.put("session", sessions);
        } else {
            reqData.put("session", new ArrayList<>());
        }

        final String s = HttpReqUtil.reqPost(context.getUrl(), JSON.toJSONString(reqData), getTimeout(), getInfo());

        if (StringUtils.isBlank(s)) {
            throw new IllegalArgumentException("模型请求失败");
        }
        return parseResult(s);
    }


    private String getUrl() {
        List<String> urls = getConfigUrls();
        if (CollectionUtils.isEmpty(urls)){
            return null;
        }
        final int index = callCount.incrementAndGet();
        if (callCount.get()>100_0000){
            callCount.set(0);
        }
        final int size = urls.size();
        final int sub = index % size;
        return urls.get(sub);
    }

    protected List<String> getConfigUrls(){
        return gptUrlList;
    }


    private String parseResult(String s) {
        if (StringUtils.isBlank(s)){
            return "";
        }
        final JSONObject jsonObject = JSON.parseObject(s);

        final JSONArray resultArr = jsonObject.getJSONArray("result");

        if (resultArr != null && resultArr.size() > 0) {
            final String chatRespText = resultArr.getString(0);
            return cleanResult(chatRespText);
        } else {
            String msg = jsonObject.getString("msg");
            if (StringUtils.isNotBlank(msg)) {
                log.error("模型调用失败,{}",msg);
                throw new IllegalArgumentException("模型调用失败");
            }
        }
        return "";
    }


    private String cleanResult(String result) {
        if (StringUtils.isBlank(replaceWords)) {
            return result;
        }
        if (StringUtils.isBlank(result)) {
            return result;
        }

        final String[] split = replaceWords.split(",");

        String target = result;

        for (String s : split) {
            if (StringUtils.isNotBlank(s)) {
                target = target.replaceAll("(?i)" + s, replaceTarget);
            }
        }
        return target;
    }


    protected int getTimeout() {
        return 40 * 1000;
    }

    protected String getInfo() {
        return "tigerBot";
    }
}
