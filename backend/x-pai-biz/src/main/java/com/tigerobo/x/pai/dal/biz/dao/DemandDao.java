package com.tigerobo.x.pai.dal.biz.dao;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.vo.biz.DemandQueryVo;
import com.tigerobo.x.pai.dal.biz.entity.DemandDo;
import com.tigerobo.x.pai.dal.biz.mapper.DemandMapper;
import com.tigerobo.x.pai.dal.biz.mapper.DemandQueryMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class DemandDao {
    @Autowired
    private DemandMapper demandMapper;
    @Autowired
    private DemandQueryMapper demandQueryMapper;



    public List<DemandDo> getByUuids(List<String> uuids){
        if (uuids == null){
            return null;
        }

        Example example = new Example(DemandDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("uuid",uuids);
        criteria.andEqualTo("isDeleted",0);
        return demandMapper.selectByExample(example);
    }
    public DemandDo getByUuid(String uuid){
        if (uuid == null){
            return null;
        }

        Example example = new Example(DemandDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uuid",uuid);
        criteria.andEqualTo("isDeleted",0);

        return demandMapper.selectOneByExample(example);
    }

    public void insert(DemandDo demandDo){
        demandMapper.insertSelective(demandDo);
    }

    public List<DemandDo> query(DemandQueryVo queryVo, String userId, List<String> groupUuids, List<String> creators){
        List<Integer> demandIdList = demandQueryMapper.getDemandIdList(userId,queryVo,groupUuids,creators);

        if (!CollectionUtils.isEmpty(demandIdList)){
            return getByOrderIds(demandIdList,queryVo.getOrderBy(),queryVo.getPageNum(),queryVo.getPageSize());
        }
        return null;
    }

    public List<DemandDo> getByOrderIds(List<Integer> ids, String orderBy,Integer pageNo,Integer pageSize){
        Example example = new Example(DemandDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        criteria.andEqualTo("isDeleted",0);

        String orderByText = "id desc";
        if (orderBy.contains("asc")){
            orderByText = "id asc";
        }
        example.setOrderByClause(orderByText);

        PageHelper.startPage(pageNo,pageSize);

        return demandMapper.selectByExample(example);
    }

    public int countUserCanView(String userId, DemandQueryVo queryVo){
        return demandQueryMapper.countUserViewDemand(userId,queryVo);
    }
    public int update(DemandDo demandDo){

        if (demandDo == null){
            return 0;
        }
        return demandMapper.updateByPrimaryKeySelective(demandDo);
    }

    public List<DemandDo> getGroupDemandList(Integer groupId){

        Example example = new Example(DemandDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("groupId",groupId);
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("id desc");

        PageHelper.startPage(1,100);
        return demandMapper.selectByExample(example);
    }
    public List<DemandDo> getUserDemandList(String createBy){

        Example example = new Example(DemandDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createBy",createBy);
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("id desc");

        PageHelper.startPage(1,100);
        return demandMapper.selectByExample(example);
    }

    public List<DemandDo> getTop() {
        Example example = new Example(DemandDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");
        PageHelper.startPage(1,10);
        return demandMapper.selectByExample(example);
    }

    public int countMine(String createBy) {
        if (StringUtils.isEmpty(createBy)){
            return 0;
        }
        Example example = new Example(DemandDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createBy",createBy);
        criteria.andEqualTo("isDeleted",0);
        return demandMapper.selectCountByExample(example);
    }
}
