package com.tigerobo.x.pai.dal.ai.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.ai.req.interact.UserInteractPublicPageReq;
import com.tigerobo.x.pai.dal.ai.entity.AiUserInteractPo;
import com.tigerobo.x.pai.dal.ai.mapper.AiUserInteractMapper;
import com.tigerobo.x.pai.dal.ai.mapper.ArtImageQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Component
public class AiUserInterActDao {

    @Autowired
    private AiUserInteractMapper aiUserInteractMapper;
    @Autowired
    private ArtImageQueryMapper artImageQueryMapper;

    public int add(AiUserInteractPo po) {
        return aiUserInteractMapper.insertSelective(po);
    }

    public void update(AiUserInteractPo po) {

        aiUserInteractMapper.updateByPrimaryKeySelective(po);
    }

    public Page<AiUserInteractPo> getPublishListQuery(UserInteractPublicPageReq req,List<Integer> supportTypes) {

        if (StringUtils.isEmpty(req.getKeyword())){
            return getPublishList(req);
        }
        PageHelper.startPage(req.getPageNum(),req.getPageSize());


        Page<Integer> list = (Page<Integer>)artImageQueryMapper.queryAiInteract(req,supportTypes);
        long total = list.getTotal();
        Page<AiUserInteractPo> page = new Page<>();
        page.setTotal(total);
        if (!CollectionUtils.isEmpty(list)){
            List<AiUserInteractPo> pos = getByIds(list,"publish_time desc");
            if (pos!=null&&pos.size()>0){
                page.addAll(pos);
            }
        }
        return page;
    }

    public Page<AiUserInteractPo> getPublishList(UserInteractPublicPageReq req) {

        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(AiUserInteractPo.class);

        Example.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(req.getBizId())){
            criteria.andEqualTo("bizId",req.getBizId());
        }
        if (req.getBizType()!=null){
            criteria.andEqualTo("bizType", req.getBizType());
        }
        criteria.andEqualTo("status",1);


        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(req.getKeyword())){
            criteria.andLike("title","%"+req.getKeyword()+"%");
        }

        example.setOrderByClause("publish_time desc");
        return (Page<AiUserInteractPo>)aiUserInteractMapper.selectByExample(example);
    }
    public Page<AiUserInteractPo> getUserPage(Integer userId, UserInteractPublicPageReq req, List<Integer> supportTypes) {


        PageHelper.startPage(req.getPageNum(), req.getPageSize());

        Example example = new Example(AiUserInteractPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        if (req.getBizType() != null) {
            criteria.andEqualTo("bizType", req.getBizType());
        }else if (supportTypes!=null&&supportTypes.size()>0){
            criteria.andIn("bizType",supportTypes);
        }

        criteria.andEqualTo("isDeleted", 0);

        if (!StringUtils.isEmpty(req.getKeyword())) {
            criteria.andLike("title", "%" + req.getKeyword() + "%");
        }

        example.setOrderByClause("id desc");
        return (Page<AiUserInteractPo>) aiUserInteractMapper.selectByExample(example);
    }
    public List<AiUserInteractPo> getByIds(List<Integer> ids){
        return getByIds(ids,null);
    }
    public List<AiUserInteractPo> getByIds(List<Integer> ids,String sort){
        Example example = new Example(AiUserInteractPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);

        criteria.andEqualTo("status",1);
        criteria.andEqualTo("isDeleted",0);
        if (!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        return aiUserInteractMapper.selectByExample(example);
    }

    public AiUserInteractPo loadByKey(String bizId,Integer bizType){
        Example example = new Example(AiUserInteractPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bizId",bizId);
        criteria.andEqualTo("bizType",bizType);
        return aiUserInteractMapper.selectOneByExample(example);
    }

    public void offline(String bizId,Integer bizType){
        final AiUserInteractPo po = loadByKey(bizId, bizType);
        if (po == null){
            return;
        }
        AiUserInteractPo update = new AiUserInteractPo();
        update.setId(po.getId());
        update.setStatus(0);
        this.update(update);
    }

    public void online(String bizId,Integer bizType,AiUserInteractPo updatePo){
        final AiUserInteractPo po = loadByKey(bizId, bizType);
        if (po == null){
            return;
        }
        AiUserInteractPo update = new AiUserInteractPo();
        update.setId(po.getId());
        update.setStatus(updatePo.getStatus());
        update.setPublishTime(updatePo.getPublishTime());
        update.setTitle(updatePo.getTitle());
        this.update(update);
    }

    public void delete(String bizId,Integer bizType){
        final AiUserInteractPo po = loadByKey(bizId, bizType);
        if (po == null){
            return;
        }
        AiUserInteractPo update = new AiUserInteractPo();
        update.setId(po.getId());
        update.setIsDeleted(true);
        this.update(update);
    }
}
