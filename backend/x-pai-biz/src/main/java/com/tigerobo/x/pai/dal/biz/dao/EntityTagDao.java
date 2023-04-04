package com.tigerobo.x.pai.dal.biz.dao;

import com.tigerobo.x.pai.api.entity.Entity;
import com.tigerobo.x.pai.dal.biz.entity.EntityTagDo;
import com.tigerobo.x.pai.dal.biz.mapper.EntityTagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Slf4j
@Component
public class EntityTagDao {


    @Autowired
    protected EntityTagMapper entityTagMapper;

    public void insert(EntityTagDo entityTagDo){
        if (entityTagDo == null){
            return;
        }
        entityTagMapper.insertSelective(entityTagDo);
    }

    public List<EntityTagDo> getDemandTags(Integer demandId){

        Example example = new Example(EntityTagDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("entityId",demandId);
        criteria.andEqualTo("entityType",2010);// Entity.Type.DEMAND.getVal());
        criteria.andEqualTo("isDeleted",0);

        return entityTagMapper.selectByExample(example);
    }

    public List<EntityTagDo> getDemandTags(List<Integer> demandIds){

        Example example = new Example(EntityTagDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("entityId",demandIds);
        criteria.andEqualTo("entityType",2010);// Entity.Type.DEMAND.getVal());
        criteria.andEqualTo("isDeleted",0);

        return entityTagMapper.selectByExample(example);
    }
    public List<EntityTagDo> getModelTags(List<Integer> modelIds){

        Example example = new Example(EntityTagDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("entityId",modelIds);
        criteria.andEqualTo("entityType",2030);
        criteria.andEqualTo("isDeleted",0);

        return entityTagMapper.selectByExample(example);
    }

    public List<EntityTagDo> getList(List<Integer> typeList, List<String> tagUidList) {
        if (CollectionUtils.isEmpty(typeList) && CollectionUtils.isEmpty(tagUidList)) {
            return null;
        }

        Example example = new Example(EntityTagDo.class);
        Example.Criteria criteria = example.createCriteria();
        if (!CollectionUtils.isEmpty(tagUidList)) {
            criteria.andIn("tagUid", tagUidList);
        }
        if (!CollectionUtils.isEmpty(typeList)) {
            criteria.andIn("entityType", typeList);

        }
        criteria.andEqualTo("isDeleted", false);

        example.selectProperties("entityId", "entityUuid", "entityType");
        example.setDistinct(true);
        return this.entityTagMapper.selectByExample(example);
    }

    public List<EntityTagDo> getTagList(String tag,Integer entityType){

        Example example = new Example(EntityTagDo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("tagUid",tag);
        criteria.andEqualTo("entityType",entityType);
        criteria.andEqualTo("isDeleted",0);

        return entityTagMapper.selectByExample(example);

    }
}
