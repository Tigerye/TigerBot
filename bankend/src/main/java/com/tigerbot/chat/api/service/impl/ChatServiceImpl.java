package com.tigerbot.chat.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.tigerbot.chat.ai.model.GptModelService;
import com.tigerbot.chat.api.service.IChatService;
import com.tigerbot.chat.model.ChatContext;
import com.tigerbot.chat.req.ChatGptReq;
import com.tigerbot.chat.service.ChatImageService;
import com.tigerbot.chat.service.LakeSensitiveService;
import com.tigerbot.chat.vo.ChatResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class ChatServiceImpl implements IChatService {



    @Value("${pai.gpt.suggestAnswer:你的问题我暂时不能回答}")
    private String suggestAnswer;

    private final GptModelService gptModelService;

    private final ChatImageService chatImageService;

    private final LakeSensitiveService lakeSensitiveService;

    public ChatServiceImpl(GptModelService gptModelService, ChatImageService chatImageService, LakeSensitiveService lakeSensitiveService) {
        this.gptModelService = gptModelService;
        this.chatImageService = chatImageService;
        this.lakeSensitiveService = lakeSensitiveService;
    }

    @Override
    public ChatResultVo chat(ChatGptReq req) {
        try {
            final ChatResultVo resultVo = doChat(req);
            return buildResult(resultVo);
        }catch (Exception ex){
            log.error("text:{}", JSON.toJSONString(req),ex);
        }
        return suggestAnswer();
    }

    private ChatResultVo doChat(ChatGptReq req) {

        Validate.isTrue(req != null&&StringUtils.isNotBlank(req.getText()), "参数为空");
        ChatContext context = initChatContext(req);
        if (lakeSensitiveService.isSensitive(req.getText())) {
            return null;
        }

        final List<String> imgs = chatImageService.chatOnImageScene(context.getText());
        if (!CollectionUtils.isEmpty(imgs)){
            ChatResultVo chatResultVo = new ChatResultVo();
            chatResultVo.setImgs(imgs);
            return chatResultVo;
        }

        String result = gptModelService.chatReq(context);
        if (StringUtils.isBlank(result)||lakeSensitiveService.isSensitive(result)) {
           return null;
        }
        ChatResultVo chatResultVo = new ChatResultVo();
        chatResultVo.setResult(result);
        return chatResultVo;
    }


    private ChatContext initChatContext(ChatGptReq req) {
        ChatContext context = new ChatContext();
        final String text = req.getText().trim();

        context.setUserId("");
        context.setUseMulti(req.isUseMulti());
        log.warn("userId:{},chat-oriText:{}", "", text);

//        final boolean chinese = CharUtil.isChinese(text);
        context.setText(text);

        context.setClientId(req.getClientSession());
        String userSession = getUserSessionId(req.getClientSession());
        context.setUserSession(userSession);
        return context;
    }

    private String getUserSessionId(String clientSession) {
        final String userId = "";
        String session = userId;
        if (StringUtils.isNotBlank(clientSession)) {
            session += "_" + clientSession;
        }
        return session;
    }

    private ChatResultVo suggestAnswer(){
        ChatResultVo vo = new ChatResultVo();
        vo.setResult(suggestAnswer);
        return vo;
    }


    private ChatResultVo buildResult(ChatResultVo input){
        if (input == null||(StringUtils.isBlank(input.getResult())&&CollectionUtils.isEmpty(input.getImgs()))){
            return suggestAnswer();
        }
        return input;
    }
}
