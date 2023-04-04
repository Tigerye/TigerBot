package com.tigerobo.x.pai.biz.ai.gpt.bo;

import com.tigerobo.x.pai.api.utils.gpt.ChatResultVo;
import lombok.Data;

@Data
public class ChatContext {

    Integer userId;
    boolean fan;
    boolean chinese;
    String text;
    String url;
    String cacheKey;

    String userSession;
    boolean useGsExample;

    boolean sensitive;

    boolean useMulti;

    String clientId;

    ChatResultVo resultVo;
}
