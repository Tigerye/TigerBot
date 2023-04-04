package com.tigerobo.x.pai.dal.ai.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.dal.ai.entity.AiMultiObjectTrackPo;
import com.tigerobo.x.pai.dal.ai.mapper.AiMultiObjectTrackMapper;
import com.tigerobo.x.pai.dal.ai.mapper.AiQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AiMultiObjectTrackDao {

    @Autowired
    private AiMultiObjectTrackMapper aiMultiObjectTrackMapper;

    @Autowired
    private AiQueryMapper aiQueryMapper;

    public AiMultiObjectTrackPo load(Integer id) {

        AiMultiObjectTrackPo po = aiMultiObjectTrackMapper.selectByPrimaryKey(id);

        if (po == null||po.getIsDeleted() == null||po.getIsDeleted()){
            return null;
        }
        return po;

    }

    public AiMultiObjectTrackPo getByReqId(Long reqId) {

        Example example = new Example(AiMultiObjectTrackPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("reqId",reqId);

        criteria.andEqualTo("isDeleted",0);
        List<AiMultiObjectTrackPo> aiMultiObjectTractPos = aiMultiObjectTrackMapper.selectByExample(example);

        return CollectionUtils.isEmpty(aiMultiObjectTractPos)?null:aiMultiObjectTractPos.get(0);
    }

    public Page<AiMultiObjectTrackPo> getPublishListQuery(ArtImagePublicPageReq req) {

        if (StringUtils.isEmpty(req.getKeyword())){
            return getPublishList(req);
        }
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        Page<Integer> list = (Page<Integer>) aiQueryMapper.query(req);
        long total = list.getTotal();
        Page<AiMultiObjectTrackPo> page = new Page<>();
        page.setTotal(total);
        if (!CollectionUtils.isEmpty(list)){
            List<AiMultiObjectTrackPo> pos = getByIds(list,"publish_time desc");
            if (pos!=null&&pos.size()>0){
                page.addAll(pos);
            }
        }
        return page;
    }

    public Page<AiMultiObjectTrackPo> getPublishList(ArtImagePublicPageReq req) {

        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(AiMultiObjectTrackPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("status",1);


        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(req.getKeyword())){
            criteria.andLike("title","%"+req.getKeyword()+"%");
        }

        example.setOrderByClause("publish_time desc");
        return (Page<AiMultiObjectTrackPo>) aiMultiObjectTrackMapper.selectByExample(example);
    }


    public List<AiMultiObjectTrackPo> getByIds(List<Integer> ids){
        return getByIds(ids,null);
    }
    public List<AiMultiObjectTrackPo> getByIds(List<Integer> ids, String sort){
        Example example = new Example(AiMultiObjectTrackPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);

        criteria.andEqualTo("status",1);
        criteria.andEqualTo("isDeleted",0);
        if (!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        return aiMultiObjectTrackMapper.selectByExample(example);
    }
    public List<Integer> getEffectByIds(List<Integer> ids, String keyword){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        Example example = new Example(AiMultiObjectTrackPo.class);

        example.selectProperties("id");

        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);

        criteria.andEqualTo("status",1);
        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(keyword)){
            criteria.andLike("title","%"+keyword+"%");
        }

        List<AiMultiObjectTrackPo> aiMultiObjectTractPos = aiMultiObjectTrackMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(aiMultiObjectTractPos)){
            return null;
        }

        return aiMultiObjectTractPos.stream().map(ai->ai.getId()).collect(Collectors.toList());
    }

    public Page<AiMultiObjectTrackPo> getUserPage(Integer userId, ArtImagePublicPageReq req) {


        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(AiMultiObjectTrackPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);

        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(req.getKeyword())){
            criteria.andLike("title","%"+req.getKeyword()+"%");
        }

        example.setOrderByClause("id desc");
        return (Page<AiMultiObjectTrackPo>) aiMultiObjectTrackMapper.selectByExample(example);
    }

    public void add(AiMultiObjectTrackPo po) {
        aiMultiObjectTrackMapper.insertSelective(po);
    }


    public void update(AiMultiObjectTrackPo po) {
        aiMultiObjectTrackMapper.updateByPrimaryKeySelective(po);
    }

    public void delete(Integer id) {

        AiMultiObjectTrackPo up = new AiMultiObjectTrackPo();
        up.setIsDeleted(true);
        up.setId(id);
        update(up);
    }

    public List<AiMultiObjectTrackPo> getDealList(Integer processStatus, int pageSize) {

        Example example = new Example(AiMultiObjectTrackPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1, pageSize);

        return aiMultiObjectTrackMapper.selectByExample(example);
    }

    public List<AiMultiObjectTrackPo> getDealListTest() {

        Example example = new Example(AiMultiObjectTrackPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",1);
//        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1, 10);

        return aiMultiObjectTrackMapper.selectByExample(example);
    }

    public int countProcessStatusList(Integer processStatus) {

        Example example = new Example(AiMultiObjectTrackPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);

        return aiMultiObjectTrackMapper.selectCountByExample(example);
    }


}
