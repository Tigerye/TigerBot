package com.tigerobo.x.pai.biz.ai.gpt;

import com.tigerobo.x.pai.biz.ai.gpt.model.GptB2ModelService;
import com.tigerobo.x.pai.biz.ai.gpt.model.GptModelBaseService;
import com.tigerobo.x.pai.biz.ai.gpt.model.GptModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ChatGptB2Service extends ChatGptBaseService {

    @Autowired
    private GptB2ModelService gptB2ModelService;
    @Override
    protected GptModelBaseService getModelService() {
        return gptB2ModelService;
    }
}
