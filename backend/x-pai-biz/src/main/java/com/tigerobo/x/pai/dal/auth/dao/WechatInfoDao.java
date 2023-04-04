package com.tigerobo.x.pai.dal.auth.dao;

import com.tigerobo.x.pai.dal.auth.entity.WechatInfoPo;
import com.tigerobo.x.pai.dal.auth.mapper.WechatInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

@Slf4j
@Component
public class WechatInfoDao {

    @Autowired
    private WechatInfoMapper wechatInfoMapper;

    public void insertOrUpdate(WechatInfoPo po){
        if (po == null){
            return;
        }
        String unionId = po.getUnionId();
        if (StringUtils.isEmpty(unionId)){
            return;
        }
        if (StringUtils.isEmpty(po.getContent())){
            return;
        }
        try {
            WechatInfoPo existDb = getByKey(unionId);
            if (existDb == null) {
                insert(po);
            } else {
                po.setId(existDb.getId());
                update(po);
            }
        }catch (Exception e){
            log.error("insertOrUpdate:"+po.getContent(),e);
        }

    }

    public WechatInfoPo getByKey(String unionId){

        Example example = new Example(WechatInfoPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("unionId",unionId);
        return wechatInfoMapper.selectOneByExample(example);

    }

    public void insert(WechatInfoPo po){
        wechatInfoMapper.insertSelective(po);
    }

    public void update(WechatInfoPo po){
        wechatInfoMapper.updateByPrimaryKeySelective(po);
    }
}
