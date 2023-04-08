package com.tigerbot.chat.controller;


import com.tigerbot.chat.api.service.IChatService;
import com.tigerbot.chat.req.ChatGptReq;
import com.tigerbot.chat.vo.ApiResultVo;
import com.tigerbot.chat.vo.ChatResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/chatGpt")
public class ChatGptController {
    @Autowired
    protected IChatService chatGptService;


    @PostMapping(path = "/execute", consumes = "application/json", produces = "application/json")
    public ApiResultVo execute(@RequestBody ChatGptReq req) {
        final ChatResultVo chat = chatGptService.chat(req);
        return buildChatResp(chat);
    }

    private ApiResultVo buildChatResp(ChatResultVo chat){
        ApiResultVo apiVo = new ApiResultVo();
        apiVo.setResult(chat.getResult());
        if (!CollectionUtils.isEmpty(chat.getImgs())){
            apiVo.getAppendInfo().put("imgs",chat.getImgs());
        }
        return apiVo;
    }

}
