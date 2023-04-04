package com.tigerobo.x.pai.biz.ai.gpt;

import com.tigerobo.x.pai.biz.ai.gpt.model.GptModelBaseService;
import com.tigerobo.x.pai.biz.ai.gpt.model.GptModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatGptService extends ChatGptBaseService {

    @Autowired
    private GptModelService gptModelService;
    @Override
    protected GptModelBaseService getModelService() {
        return gptModelService;
    }
}
