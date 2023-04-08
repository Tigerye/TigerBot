package com.tigerobo.chat;

import com.alibaba.fastjson.JSON;
import com.tigerbot.chat.ai.model.GptModelService;
import com.tigerbot.chat.api.service.IChatService;
import com.tigerbot.chat.req.ChatGptReq;
import com.tigerbot.chat.vo.ChatResultVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ChatServiceTest extends BaseTest {


    @Autowired
    private IChatService chatService;

    @Autowired
    private GptModelService gptModelService;

    @Test
    public void singleChatTest(){
        ChatGptReq req =new ChatGptReq();
        req.setText("中国的首都在哪里");
        req.setClientSession(String.valueOf(System.currentTimeMillis()/1000));
        final ChatResultVo chat = chatService.chat(req);
        System.out.println(JSON.toJSONString(chat));
    }

    @Test
    public void multiChatTest(){
        final String text = "中国的首都在哪里";
        ask(text,true);
        ask("美国呢",true);
    }

    private void ask(String text,boolean useMulti) {
        ChatGptReq req =new ChatGptReq();

        req.setText(text);
        req.setUseMulti(useMulti);
        req.setClientSession(String.valueOf(System.currentTimeMillis()/1000));
        final ChatResultVo chat = chatService.chat(req);
        System.out.println(JSON.toJSONString(chat));
    }
}
