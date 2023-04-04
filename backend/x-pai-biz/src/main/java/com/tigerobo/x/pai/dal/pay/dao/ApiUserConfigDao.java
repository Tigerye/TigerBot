package com.tigerobo.x.pai.dal.pay.dao;

import com.tigerobo.x.pai.dal.pay.entity.ApiUserConfigPo;
import com.tigerobo.x.pai.dal.pay.mapper.ApiUserConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class ApiUserConfigDao {

    @Autowired
    private ApiUserConfigMapper apiUserConfigMapper;

    public int add(ApiUserConfigPo po){
        return apiUserConfigMapper.insertSelective(po);
    }

    public int update(ApiUserConfigPo po){
        return apiUserConfigMapper.updateByPrimaryKeySelective(po);
    }

    public ApiUserConfigPo load(Integer id){

        final ApiUserConfigPo po = apiUserConfigMapper.selectByPrimaryKey(id);

        if (po == null||po.getIsDeleted()==null||po.getIsDeleted()){
            return null;
        }
        return po;
    }


    public List<ApiUserConfigPo> getAll(){

        Example example = new Example(ApiUserConfigPo.class);

        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted",0);
        return apiUserConfigMapper.selectByExample(example);
    }
}
