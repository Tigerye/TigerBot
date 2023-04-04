package com.tigerobo.x.pai.biz.ai.gpt;

import com.tigerobo.x.pai.api.ai.req.gpt.ChatGptReq;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.api.utils.gpt.ChatResultVo;
import com.tigerobo.x.pai.biz.ai.gpt.bo.ChatContext;
import com.tigerobo.x.pai.biz.ai.gpt.model.GptModelBaseService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Slf4j
public abstract class ChatGptBaseService {

    @Value("${pai.gpt.suggestAnswer:你的问题我暂时不能回答}")
    private String suggestAnswer;
    @Autowired
    private ChatSensitiveService chatSensitiveService;
    @Autowired
    private ChatStoreService chatStoreService;
    @Autowired
    private ChatImageService chatImageService;
    @Autowired
    private GansuChatService gansuChatService;


    public ApiResultVo chat(ChatGptReq req) {
        Validate.isTrue(req != null&&StringUtils.isNotBlank(req.getText()), "参数为空");
        final ChatContext context = doChat(req);
        return buildResult(context);
    }


    private ChatContext doChat(ChatGptReq req) {
        ChatContext context = initChatContext(req);
        chatStoreService.save(context);

        if (chatSensitiveService.isSensitive(context.getText())) {
            log.warn("question:{},sensitive", req.getText());
            return null;
        }
        final String gsQaResult = gansuChatService.getResult(context.getText());
        if (StringUtils.isNotBlank(gsQaResult)){
            context.getResultVo().setResult(gsQaResult);
            return context;
        }

        final List<String> imgs = chatImageService.produceImages(context.getText());
        if (!CollectionUtils.isEmpty(imgs)){
            context.getResultVo().setImgs(imgs);
            return context;
        }

        final String result = getModelService().chatReq(context);
        if (chatSensitiveService.isSensitive(result)) {
            log.warn("question:{},result sensitive,{}", req.getText(),result);
            return null;
        }

        if (StringUtils.isNotBlank(result)) {
            context.getResultVo().setResult(result);
        }
        return context;
    }

    private ApiResultVo buildResult(ChatContext context){
        ApiResultVo vo = new ApiResultVo();
        List<String> resultList = new ArrayList<>();
        vo.setResult(resultList);
        if (context == null||context.getResultVo()==null){
            resultList.add(suggestAnswer);
            return vo;
        }
        final ChatResultVo resultVo = context.getResultVo();
        if (StringUtils.isNotBlank(resultVo.getResult())){
            resultList.add(resultVo.getResult());
        }else if (!CollectionUtils.isEmpty(resultVo.getImgs())){
            vo.getAppendInfo().put("imgs",resultVo.getImgs());
        }else {
            resultList.add(suggestAnswer);
        }
        return vo;
    }
    private ChatContext initChatContext(ChatGptReq req) {
        ChatContext context = new ChatContext();
        final String text = req.getText().trim();
        final Integer userId = ThreadLocalHolder.getUserId();
        context.setUserId(userId);
        context.setUseMulti(req.isUseMulti());
        log.warn("userId:{},chat-oriText:{}", userId, text);
        context.setText(text);

        context.setClientId(req.getClientSession());

        context.setResultVo(new ChatResultVo());
        return context;
    }
    protected abstract GptModelBaseService getModelService();

}
