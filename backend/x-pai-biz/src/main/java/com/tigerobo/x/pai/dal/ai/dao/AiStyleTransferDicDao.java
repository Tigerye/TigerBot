package com.tigerobo.x.pai.dal.ai.dao;

import com.tigerobo.x.pai.dal.ai.entity.AiParamDictPo;
import com.tigerobo.x.pai.dal.ai.entity.AiStyleTransferDicPo;
import com.tigerobo.x.pai.dal.ai.mapper.AiStyleTransferDicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class AiStyleTransferDicDao {

    @Autowired
    private AiStyleTransferDicMapper aiStyleTransferDicMapper;


    //1-art_image
    public List<AiStyleTransferDicPo> getList() {

        Example example = new Example(AiParamDictPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted", 0);
        return aiStyleTransferDicMapper.selectByExample(example);
    }
}
