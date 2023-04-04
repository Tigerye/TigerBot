package com.tigerobo.x.pai.dal.ai.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.dal.ai.entity.AiSpatioActionPo;
import com.tigerobo.x.pai.dal.ai.mapper.AiSpatioActionMapper;
import com.tigerobo.x.pai.dal.ai.mapper.SpatioActionQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AiSpatioActionDao {

    @Autowired
    private AiSpatioActionMapper aiSpatioActionMapper;

    @Autowired
    private SpatioActionQueryMapper styleTransferQueryMapper;
    public AiSpatioActionPo load(Integer id) {

        AiSpatioActionPo po = aiSpatioActionMapper.selectByPrimaryKey(id);

        if (po == null||po.getIsDeleted() == null||po.getIsDeleted()){
            return null;
        }
        return po;

    }

    public AiSpatioActionPo getByReqId(Long reqId) {

        Example example = new Example(AiSpatioActionPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("reqId",reqId);

        criteria.andEqualTo("isDeleted",0);
        List<AiSpatioActionPo> aiSpatioActionPos = aiSpatioActionMapper.selectByExample(example);

        return CollectionUtils.isEmpty(aiSpatioActionPos)?null:aiSpatioActionPos.get(0);
    }

    public Page<AiSpatioActionPo> getPublishListQuery(ArtImagePublicPageReq req) {

        if (StringUtils.isEmpty(req.getKeyword())){
            return getPublishList(req);
        }
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        Page<Integer> list = (Page<Integer>)styleTransferQueryMapper.query(req);
        long total = list.getTotal();
        Page<AiSpatioActionPo> page = new Page<>();
        page.setTotal(total);
        if (!CollectionUtils.isEmpty(list)){
            List<AiSpatioActionPo> pos = getByIds(list,"publish_time desc");
            if (pos!=null&&pos.size()>0){
                page.addAll(pos);
            }
        }
        return page;
    }

    public Page<AiSpatioActionPo> getPublishList(ArtImagePublicPageReq req) {

        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(AiSpatioActionPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("status",1);


        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(req.getKeyword())){
            criteria.andLike("title","%"+req.getKeyword()+"%");
        }

        example.setOrderByClause("publish_time desc");
        return (Page<AiSpatioActionPo>)aiSpatioActionMapper.selectByExample(example);
    }


    public List<AiSpatioActionPo> getByIds(List<Integer> ids){
        return getByIds(ids,null);
    }
    public List<AiSpatioActionPo> getByIds(List<Integer> ids,String sort){
        Example example = new Example(AiSpatioActionPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);

        criteria.andEqualTo("status",1);
        criteria.andEqualTo("isDeleted",0);
        if (!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        return aiSpatioActionMapper.selectByExample(example);
    }
    public List<Integer> getEffectByIds(List<Integer> ids, String keyword){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        Example example = new Example(AiSpatioActionPo.class);

        example.selectProperties("id");

        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);

        criteria.andEqualTo("status",1);
        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(keyword)){
            criteria.andLike("title","%"+keyword+"%");
        }

        List<AiSpatioActionPo> aiSpatioActionPos = aiSpatioActionMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(aiSpatioActionPos)){
            return null;
        }

        return aiSpatioActionPos.stream().map(ai->ai.getId()).collect(Collectors.toList());
    }

    public Page<AiSpatioActionPo> getUserPage(Integer userId, ArtImagePublicPageReq req) {


        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(AiSpatioActionPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);

        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(req.getKeyword())){
            criteria.andLike("title","%"+req.getKeyword()+"%");
        }

        example.setOrderByClause("id desc");
        return (Page<AiSpatioActionPo>)aiSpatioActionMapper.selectByExample(example);
    }

    public void add(AiSpatioActionPo po) {
        aiSpatioActionMapper.insertSelective(po);
    }


    public void update(AiSpatioActionPo po) {
        aiSpatioActionMapper.updateByPrimaryKeySelective(po);
    }

    public void delete(Integer id) {

        AiSpatioActionPo up = new AiSpatioActionPo();
        up.setIsDeleted(true);
        up.setId(id);
        update(up);
    }

    public List<AiSpatioActionPo> getDealList(Integer processStatus,int pageSize) {

        Example example = new Example(AiSpatioActionPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1, pageSize);

        return aiSpatioActionMapper.selectByExample(example);
    }

    public List<AiSpatioActionPo> getDealListTest() {

        Example example = new Example(AiSpatioActionPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",515);
//        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1, 10);

        return aiSpatioActionMapper.selectByExample(example);
    }

    public int countProcessStatusList(Integer processStatus) {

        Example example = new Example(AiSpatioActionPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);

        return aiSpatioActionMapper.selectCountByExample(example);
    }


}
