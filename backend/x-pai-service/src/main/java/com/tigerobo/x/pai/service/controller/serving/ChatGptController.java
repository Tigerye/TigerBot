package com.tigerobo.x.pai.service.controller.serving;

import com.tigerobo.x.pai.api.ai.req.gpt.ChatGptReq;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.biz.ai.gpt.ChatGptB2Service;
import com.tigerobo.x.pai.biz.ai.gpt.ChatGptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/chatGpt")
@Api(value = "ChatGpt", position = 3100, tags = "ChatGpt API请求接口")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class ChatGptController {
    @Autowired
    protected ChatGptService chatGptService;

    @Autowired
    protected ChatGptB2Service chatGptB2Service;


    @ApiOperation(value = "执行")
    @PostMapping(path = "/execute", consumes = "application/json", produces = "application/json")
    public ApiResultVo execute(@Valid @RequestBody ChatGptReq req) {
        return chatGptService.chat(req);
    }


    @ApiOperation(value = "大模型b2执行")
    @PostMapping(path = "/b2/execute", consumes = "application/json", produces = "application/json")
    public ApiResultVo b2Execute(@Valid @RequestBody ChatGptReq req) {
        return chatGptB2Service.chat(req);
    }

}
