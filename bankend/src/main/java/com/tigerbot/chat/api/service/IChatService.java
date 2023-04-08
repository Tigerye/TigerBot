package com.tigerbot.chat.api.service;

import com.tigerbot.chat.req.ChatGptReq;
import com.tigerbot.chat.vo.ChatResultVo;

public interface IChatService {

    ChatResultVo chat(ChatGptReq req);
}
