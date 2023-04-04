package com.tigerobo.x.pai.dal.ai.dao;

import com.tigerobo.x.pai.dal.ai.entity.AiChatQaPo;
import com.tigerobo.x.pai.dal.ai.mapper.AiChatQaMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class AiChatQaDao {

    @Resource
    private AiChatQaMapper aiChatQaMapper;

    
    public void add(AiChatQaPo po) {
        aiChatQaMapper.insertSelective(po);
    }

    public int update(AiChatQaPo po) {
        return aiChatQaMapper.updateByPrimaryKeySelective(po);
    }


}
