package com.tigerobo.x.pai.dal.ai.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.tigerobo.x.pai.api.ai.req.AiArtImageQueryReq;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.dal.ai.entity.AiStyleTransferPo;
import com.tigerobo.x.pai.dal.ai.mapper.AiStyleTransferMapper;

import com.tigerobo.x.pai.dal.ai.mapper.StyleTransferQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AiStyleTransferDao {

    @Autowired
    private AiStyleTransferMapper aiStyleTransferMapper;

    @Autowired
    private StyleTransferQueryMapper styleTransferQueryMapper;
    public AiStyleTransferPo load(Integer id) {

        AiStyleTransferPo po = aiStyleTransferMapper.selectByPrimaryKey(id);

        if (po == null||po.getIsDeleted() == null||po.getIsDeleted()){
            return null;
        }
        return po;

    }

    public AiStyleTransferPo getByReqId(Long reqId) {

        Example example = new Example(AiStyleTransferPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("reqId",reqId);

        criteria.andEqualTo("isDeleted",0);
        List<AiStyleTransferPo> aiStyleTransferPos = aiStyleTransferMapper.selectByExample(example);

        return CollectionUtils.isEmpty(aiStyleTransferPos)?null:aiStyleTransferPos.get(0);
    }

    public Page<AiStyleTransferPo> getPublishListQuery(ArtImagePublicPageReq req) {

        if (StringUtils.isEmpty(req.getKeyword())){
            return getPublishList(req);
        }
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        Page<Integer> list = (Page<Integer>)styleTransferQueryMapper.query(req);
        long total = list.getTotal();
        Page<AiStyleTransferPo> page = new Page<>();
        page.setTotal(total);
        if (!CollectionUtils.isEmpty(list)){
            List<AiStyleTransferPo> pos = getByIds(list,"publish_time desc");
            if (pos!=null&&pos.size()>0){
                page.addAll(pos);
            }
        }
        return page;
    }

    public Page<AiStyleTransferPo> getPublishList(ArtImagePublicPageReq req) {

        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(AiStyleTransferPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("status",1);


        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(req.getKeyword())){
            criteria.andLike("title","%"+req.getKeyword()+"%");
        }

        example.setOrderByClause("publish_time desc");
        return (Page<AiStyleTransferPo>)aiStyleTransferMapper.selectByExample(example);
    }


    public List<AiStyleTransferPo> getByIds(List<Integer> ids){
        return getByIds(ids,null);
    }
    public List<AiStyleTransferPo> getByIds(List<Integer> ids,String sort){
        Example example = new Example(AiStyleTransferPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);

        criteria.andEqualTo("status",1);
        criteria.andEqualTo("isDeleted",0);
        if (!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        return aiStyleTransferMapper.selectByExample(example);
    }
    public List<Integer> getEffectByIds(List<Integer> ids, String keyword){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        Example example = new Example(AiStyleTransferPo.class);

        example.selectProperties("id");

        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);

        criteria.andEqualTo("status",1);
        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(keyword)){
            criteria.andLike("title","%"+keyword+"%");
        }

        List<AiStyleTransferPo> aiStyleTransferPos = aiStyleTransferMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(aiStyleTransferPos)){
            return null;
        }

        return aiStyleTransferPos.stream().map(ai->ai.getId()).collect(Collectors.toList());
    }

    public Page<AiStyleTransferPo> getUserPage(Integer userId, ArtImagePublicPageReq req) {


        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(AiStyleTransferPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);

        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(req.getKeyword())){
            criteria.andLike("title","%"+req.getKeyword()+"%");
        }

        example.setOrderByClause("id desc");
        return (Page<AiStyleTransferPo>)aiStyleTransferMapper.selectByExample(example);
    }

    public void add(AiStyleTransferPo po) {
        aiStyleTransferMapper.insertSelective(po);
    }


    public void update(AiStyleTransferPo po) {
        aiStyleTransferMapper.updateByPrimaryKeySelective(po);
    }

    public void delete(Integer id) {

        AiStyleTransferPo up = new AiStyleTransferPo();
        up.setIsDeleted(true);
        up.setId(id);
        update(up);
    }

    public List<AiStyleTransferPo> getDealList(Integer processStatus,int pageSize) {

        Example example = new Example(AiStyleTransferPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1, pageSize);

        return aiStyleTransferMapper.selectByExample(example);
    }

    public int countProcessStatusList(Integer processStatus) {

        Example example = new Example(AiStyleTransferPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);

        return aiStyleTransferMapper.selectCountByExample(example);
    }


}
