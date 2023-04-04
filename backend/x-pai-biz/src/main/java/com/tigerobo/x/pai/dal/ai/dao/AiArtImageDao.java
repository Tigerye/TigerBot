package com.tigerobo.x.pai.dal.ai.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import com.tigerobo.x.pai.dal.ai.mapper.AiArtImageMapper;
import com.tigerobo.x.pai.dal.ai.mapper.ArtImageQueryMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AiArtImageDao {

    @Autowired
    private AiArtImageMapper aiArtImageMapper;

    @Autowired
    private ArtImageQueryMapper artImageQueryMapper;

    public void incrThumb(Integer id){

        artImageQueryMapper.incrThumb(id);
    }

    public void decrThumb(Integer id){

        artImageQueryMapper.decrThumb(id);
    }


    public List<AiArtImagePo> getUserInputImageHistory(Integer userId){

        Example example = new Example(AiArtImagePo.class);

        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);

        final Date startDate = DateUtils.addDays(new Date(), -30);
        criteria.andGreaterThan("createTime",startDate);
        criteria.andNotEqualTo("inputImage","");

        PageHelper.startPage(1,10);
        example.setOrderByClause("id desc");

        return aiArtImageMapper.selectByExample(example);
    }

    public AiArtImagePo load(Integer id) {

        AiArtImagePo po = aiArtImageMapper.selectByPrimaryKey(id);

        if (po == null||po.getIsDeleted() == null||po.getIsDeleted()){
            return null;
        }
        return po;
    }

    public int countUserDayUse(int userId, Date date){
        Example example = new Example(AiArtImagePo.class);

        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andGreaterThanOrEqualTo("createTime",date);
        criteria.andEqualTo("useFree",1);
        criteria.andNotEqualTo("processStatus",2);

        return aiArtImageMapper.selectCountByExample(example);
    }

    public AiArtImagePo getByReqId(Long reqId) {

        Example example = new Example(AiArtImagePo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("reqId",reqId);

        criteria.andEqualTo("isDeleted",0);
        List<AiArtImagePo> aiArtImagePos = aiArtImageMapper.selectByExample(example);

        return CollectionUtils.isEmpty(aiArtImagePos)?null:aiArtImagePos.get(0);
    }

    public Page<AiArtImagePo> getPublishListQuery(ArtImagePublicPageReq req, String order) {

//        if (StringUtils.isEmpty(req.getKeyword())){
//            return getPublishList(req,order);
//        }
//        order = "ai."+order;
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        Page<Integer> list = (Page<Integer>)artImageQueryMapper.queryNew(req,order);
        long total = list.getTotal();
        Page<AiArtImagePo> page = new Page<>();
        page.setTotal(total);
        if (!CollectionUtils.isEmpty(list)){
            List<AiArtImagePo> pos = getByIds(list,null);
            if (pos!=null&&pos.size()>0){

                final Map<Integer, AiArtImagePo> poMap = getIdPoMap(pos);

                final List<AiArtImagePo> collect = list.stream().map(id -> poMap.get(id)).filter(Objects::nonNull).collect(Collectors.toList());
                page.addAll(collect);
            }
        }
        return page;
    }

    private Map<Integer, AiArtImagePo> getIdPoMap(List<AiArtImagePo> pos) {
        Map<Integer,AiArtImagePo> poMap = new HashMap<>();
        for (AiArtImagePo po : pos) {
            poMap.put(po.getId(),po);
        }
        return poMap;
    }

    @Deprecated
    public Page<AiArtImagePo> getPublishList(ArtImagePublicPageReq req, String order) {

        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(AiArtImagePo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("status",1);


        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(req.getKeyword())){
            criteria.andLike("title","%"+req.getKeyword()+"%");
        }

        if (!StringUtils.isEmpty(req.getModifier())){
            criteria.andLike("modifiers","%\""+req.getModifier()+"\"%");
        }

        if (StringUtils.isEmpty(order)){
            example.setOrderByClause("publish_time desc");
        }else {
            example.setOrderByClause(order);
        }

        return (Page<AiArtImagePo>)aiArtImageMapper.selectByExample(example);
    }
    public List<AiArtImagePo> getByIds(List<Integer> ids){
        return getByIds(ids,null);
    }
    public List<AiArtImagePo> getByIds(List<Integer> ids,String sort){
        Example example = new Example(AiArtImagePo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);

        criteria.andEqualTo("status",1);
        criteria.andEqualTo("isDeleted",0);
        if (!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        return aiArtImageMapper.selectByExample(example);
    }
    public List<Integer> getEffectByIds(List<Integer> ids, String keyword){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        Example example = new Example(AiArtImagePo.class);

        example.selectProperties("id");

        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);

        criteria.andEqualTo("status",1);
        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(keyword)){
            criteria.andLike("title","%"+keyword+"%");
        }

        List<AiArtImagePo> aiArtImagePos = aiArtImageMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(aiArtImagePos)){
            return null;
        }

        return aiArtImagePos.stream().map(ai->ai.getId()).collect(Collectors.toList());
    }

    public Page<AiArtImagePo> getUserPage(Integer userId, ArtImagePublicPageReq req) {


        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(AiArtImagePo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);

        if (req.getStartCreateTime()!=null){
            criteria.andGreaterThanOrEqualTo("createTime",req.getStartCreateTime());
        }
        if (req.getProcessStatus()!=null){
            criteria.andEqualTo("processStatus",req.getProcessStatus());
        }
        if (req.getStatus()!=null){
            criteria.andEqualTo("status",req.getStatus());
        }
        if (req.getStyleTypeId()!=null){
            criteria.andEqualTo("styleType",req.getStyleTypeId());
        }
        criteria.andEqualTo("isDeleted",0);

        if (!StringUtils.isEmpty(req.getKeyword())){
            criteria.andLike("text","%"+req.getKeyword()+"%");
        }

        if (!StringUtils.isEmpty(req.getModifier())){
            String modifier = "\""+req.getModifier()+"\"";
            criteria.andLike("modifiers","%"+modifier+"%");
        }

        example.setOrderByClause("id desc");
        return (Page<AiArtImagePo>)aiArtImageMapper.selectByExample(example);
    }

    public void add(AiArtImagePo po) {
        aiArtImageMapper.insertSelective(po);
    }


    public int update(AiArtImagePo po) {
        return aiArtImageMapper.updateByPrimaryKeySelective(po);
    }

    public void delete(Integer id) {

        AiArtImagePo up = new AiArtImagePo();
        up.setIsDeleted(true);
        up.setId(id);
        update(up);
    }


    public List<AiArtImagePo> getUserDealList(List<Integer> userIds,Integer processStatus,int pageSize) {

        Example example = new Example(AiArtImagePo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("userId",userIds);
        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1, pageSize);
        example.setOrderByClause("id asc");

        return aiArtImageMapper.selectByExample(example);
    }


    public List<AiArtImagePo> getDealList(Integer processStatus,int pageSize) {

        Example example = new Example(AiArtImagePo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1, pageSize);

        return aiArtImageMapper.selectByExample(example);
    }


    public List<AiArtImagePo> getDealListByCondition(Integer processStatus,int pageSize,List<Integer> excludeUserIds) {

        Example example = new Example(AiArtImagePo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);
        if (!CollectionUtils.isEmpty(excludeUserIds)){
            criteria.andNotIn("userId",excludeUserIds);
        }

        PageHelper.startPage(1, pageSize);
        return aiArtImageMapper.selectByExample(example);
    }

    public List<AiArtImagePo> getDealListTest(int pageSize) {

        Example example = new Example(AiArtImagePo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", 136273);
        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1, pageSize);

        return aiArtImageMapper.selectByExample(example);
    }

    public int countProcessStatusList(Integer processStatus) {

        Example example = new Example(AiArtImagePo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("processStatus", processStatus);
        criteria.andEqualTo("isDeleted",0);

        return aiArtImageMapper.selectCountByExample(example);
    }


}
