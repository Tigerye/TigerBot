package com.tigerobo.x.pai.biz.ai.gpt;

import com.tigerobo.x.pai.biz.ai.gpt.bo.ChatContext;
import com.tigerobo.x.pai.biz.utils.ThreadUtil;
import com.tigerobo.x.pai.dal.ai.dao.AiChatQaDao;
import com.tigerobo.x.pai.dal.ai.entity.AiChatQaPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatStoreService {

    @Autowired
    private AiChatQaDao aiChatQaDao;

    public void save(ChatContext context){

        ThreadUtil.asyncDbExecutor.execute(() -> doSave(context));
    }

    public void doSave(ChatContext context){
        try {
            final AiChatQaPo po = convert(context);
            aiChatQaDao.add(po);
        }catch (Exception ex){
            log.error("content:{}",context.getText(),ex);
        }
    }

    private AiChatQaPo convert(ChatContext context){
        AiChatQaPo po = new AiChatQaPo();

        po.setQuestion(context.getText());
        po.setClientId(context.getClientId());
        po.setUserId(context.getUserId());

        return po;
    }
}
