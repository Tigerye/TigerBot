package com.tigerbot.chat.model;


import lombok.Data;

/**
 * 问答运行上下文
 */
@Data
public class ChatContext {

    String userId;
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
}
