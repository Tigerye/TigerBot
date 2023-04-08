package com.tigerbot.chat.req;

import lombok.Data;

@Data
public class ChatGptReq {

    //问句
    String text;

    //会话
    String clientSession;

    //是否使用多轮
    boolean useMulti = false;
}