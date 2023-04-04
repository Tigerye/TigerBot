package com.tigerobo.x.pai.dal.auth.dao;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.dal.auth.entity.FollowPo;
import com.tigerobo.x.pai.dal.auth.mapper.FollowMapper;
import com.tigerobo.x.pai.dal.biz.entity.user.UserCommentPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Component
public class FollowDao {

    @Autowired(required = false)
    private FollowMapper followMapper;
    public void add(FollowPo followPo){
        followMapper.insertSelective(followPo);
    }

    public List<FollowPo> getFollowByBizIdList(Integer userId,List<Integer> bizIds,Integer type){
        Example example = new Example(FollowPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        if (!CollectionUtils.isEmpty(bizIds)){
            criteria.andIn("bizId",bizIds);
        }
        if (type!=null){
            criteria.andEqualTo("bizType",type);
        }
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("id desc");
        PageHelper.startPage(1,200);
        return followMapper.selectByExample(example);
    }



    public int countFanUnRead(Integer userId, Date preTime) {
        if (userId == null){
            return 0;
        }
        Example example = new Example(FollowPo.class);
        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("bizId",userId);
        criteria.andEqualTo("bizType",0);
        criteria.andEqualTo("isDeleted",0);
        if (preTime!=null){
            criteria.andGreaterThan("createTime",preTime);
        }
        return followMapper.selectCountByExample(example);

    }


    public List<FollowPo> getUserFans(Integer userId,PageReqVo req){
        Example example = new Example(FollowPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bizId",userId);
        criteria.andEqualTo("bizType",0);
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("id desc");

        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        return followMapper.selectByExample(example);
    }

    public List<FollowPo> getFollowByTypes(Integer userId,Integer bizId,List<Integer> types){
        Example example = new Example(FollowPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        if (bizId!=null){
            criteria.andEqualTo("bizId",bizId);
        }
        if (types!=null&&types.size()>0){
            criteria.andIn("bizType",types);
        }
        criteria.andEqualTo("isDeleted",0);
        PageHelper.startPage(1,100);
        example.setOrderByClause("id desc");
        return followMapper.selectByExample(example);
    }

    public List<FollowPo> getFollowByKey(Integer userId,Integer bizId,Integer type){
        Example example = new Example(FollowPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        if (bizId!=null){
            criteria.andEqualTo("bizId",bizId);
        }
        if (type!=null){
            criteria.andEqualTo("bizType",type);
        }
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("id desc");
        return followMapper.selectByExample(example);
    }

    public void cancelFollow(Integer userId,Integer bizId,Integer type){

        FollowPo po = new FollowPo();
        po.setIsDeleted(true);

        Example example = new Example(FollowPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("bizId",bizId);
        criteria.andEqualTo("bizType",type);
        criteria.andEqualTo("isDeleted",0);
        followMapper.updateByExampleSelective(po,example);
    }


    public int countByType(Integer userId,Integer type){

        Example example = new Example(FollowPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        if (type!=null){
            criteria.andEqualTo("bizType",type);
        }

        criteria.andEqualTo("isDeleted",0);
        return followMapper.selectCountByExample(example);
    }

    public int countByUser(Integer userId){

        Example example = new Example(FollowPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("isDeleted",0);
        return followMapper.selectCountByExample(example);
    }
}
