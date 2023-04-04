package com.tigerobo.x.pai.dal.auth.dao;

import com.tigerobo.x.pai.dal.auth.entity.OrgInfoPo;
import com.tigerobo.x.pai.dal.auth.mapper.OrgInfoMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class OrgInfoDao {

    @Autowired
    private OrgInfoMapper orgInfoMapper;

    public void add(OrgInfoPo po){
        if (po==null){
            return;
        }
        orgInfoMapper.insertSelective(po);
    }

    public void update(OrgInfoPo po){

        if (po==null||po.getId()==null){
            return;
        }
        orgInfoMapper.updateByPrimaryKeySelective(po);
    }
    public OrgInfoPo load(Integer id){
        if (id == null){
            return null;
        }
        return orgInfoMapper.selectByPrimaryKey(id);
    }

    public OrgInfoPo getByUserId(Integer userId){
        if (userId == null){
            return null;
        }

        Example example = new Example(OrgInfoPo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");

        List<OrgInfoPo> orgInfoPos = orgInfoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(orgInfoPos)){
            return null;
        }
        return orgInfoPos.get(0);
    }
}
