package com.tigerobo.pai.biz.test.ai;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.ai.req.gpt.ChatGptReq;
import com.tigerobo.x.pai.biz.ai.gpt.ChatGptB2Service;
import com.tigerobo.x.pai.biz.ai.gpt.ChatGptService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class ChatGptServiceTest extends BaseTest {

    @Autowired
    private ChatGptService chatGptService;

    @Autowired
    private ChatGptB2Service chatGptB2Service;

    @Test
    public void test(){

        req();
//        req();
    }

    @Test
    public void chatTest(){
        ThreadLocalHolder.setUserId(3);
        String client = DateFormatUtils.format(new Date(),"HHmmss");
        doReq("中国的首都在哪里",client,false);
//        doReq("画个兔子",client,false);

    }

    private void req() {
        ThreadLocalHolder.setUserId(3);
        final long start = System.currentTimeMillis();
        String client = DateFormatUtils.format(new Date(),"HHmmss");
        doReq("习近平哪里人?",client);
        doReq("where is the capital of China?",client);
//        doReq("多取几个",client);
        doReq("Where about US?",client);
        doReq("JAPAN??",client);

        doReq("ENGLAND?",client);
        doReq("FRENCH?",client);
        doReq("Germany?",client);
        doReq("how to get money?",client);
        doReq("where is shanghai?",client);
        doReq("中国怎么样?",client);
        doReq("How about Chinese?",client);
        doReq("泰国哪里漂亮?",client);
    }
    private void doReq(String text,String client) {
        doReq(text,client,true);
    }
    private void doReq(String text,String client,boolean useMulti) {
        ChatGptReq req = new ChatGptReq();
        req.setClientSession(client);
        req.setText(text);
        req.setUseMulti(useMulti);

        final Object map = chatGptService.chat(req);
        System.out.println(JSON.toJSONString(map));
    }
}
