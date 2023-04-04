package com.tigerobo.x.pai.api.ai.req.gpt;

import lombok.Data;

@Data
public class ChatGptReq {
    String chatType;
    String text;
    Integer maxLength;

    String clientSession;

    boolean useMulti = false;
}
