package com.tigerobo.x.pai.dal.admin.dao;

import com.tigerobo.x.pai.dal.admin.entity.SsoUserPo;
import com.tigerobo.x.pai.dal.admin.mapper.SsoUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

@Component
public class SsoUserDao {

    @Autowired
    private SsoUserMapper ssoUserMapper;

    public void add(SsoUserPo ssoUserPo){
        ssoUserMapper.insertSelective(ssoUserPo);
    }

    public void update(SsoUserPo po){
        ssoUserMapper.updateByPrimaryKeySelective(po);
    }


    public SsoUserPo load(int id){

        SsoUserPo po = ssoUserMapper.selectByPrimaryKey(id);

        if (po!=null&&po.getIsDeleted()!=null&&!po.getIsDeleted()){
            return po;
        }
        return null;
    }
    public SsoUserPo getByUserName(String userName){

        if (StringUtils.isEmpty(userName)){
            return null;
        }

        Example example = new Example(SsoUserPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userName",userName);
        criteria.andEqualTo("isDeleted",0);

        return ssoUserMapper.selectOneByExample(example);
    }

}
