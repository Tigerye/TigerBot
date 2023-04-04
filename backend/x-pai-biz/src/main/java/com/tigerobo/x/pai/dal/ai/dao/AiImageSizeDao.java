package com.tigerobo.x.pai.dal.ai.dao;

import com.tigerobo.x.pai.dal.ai.entity.AiImageSizePo;
import com.tigerobo.x.pai.dal.ai.mapper.AiImageSizeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class AiImageSizeDao {

    @Autowired
    private AiImageSizeMapper aiImageSizeMapper;


    public AiImageSizePo load(Integer id){

        final AiImageSizePo po = aiImageSizeMapper.selectByPrimaryKey(id);
        if (po==null||po.getIsDeleted()){
            return null;
        }
        return po;
    }
    public List<AiImageSizePo> getAll(){

        Example example = new Example(AiImageSizePo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("seq");
        return aiImageSizeMapper.selectByExample(example);

    }
}
