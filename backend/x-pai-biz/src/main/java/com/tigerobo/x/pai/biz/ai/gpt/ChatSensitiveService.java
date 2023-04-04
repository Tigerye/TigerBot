package com.tigerobo.x.pai.biz.ai.gpt;

import com.tigerobo.x.pai.biz.lake.LakeSensitiveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatSensitiveService {

    @Value("${pai.gpt.useSensitive:false}")
    private boolean useSensitive;

    @Autowired
    private LakeSensitiveService lakeSensitiveService;

    public boolean isSensitive(String text){
        if (!useSensitive){
            return false;
        }
        try {
            String label = lakeSensitiveService.getHupuLabel(text);
            return StringUtils.isNotBlank(label);
        }catch (Exception ex){
            log.error("",ex);
        }
        return false;
    }
}
