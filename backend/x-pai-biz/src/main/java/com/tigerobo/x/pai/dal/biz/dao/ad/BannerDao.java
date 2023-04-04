package com.tigerobo.x.pai.dal.biz.dao.ad;

import com.tigerobo.x.pai.dal.biz.entity.ad.AdBannerPo;
import com.tigerobo.x.pai.dal.biz.mapper.ad.AdBannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class BannerDao {


    @Autowired
    private AdBannerMapper adBannerMapper;
    public void add(AdBannerPo po){

        adBannerMapper.insertSelective(po);
    }

    public int update(AdBannerPo po){
        return adBannerMapper.updateByPrimaryKeySelective(po);
    }

    public List<AdBannerPo> getBannerList(){


        Example example = new Example(AdBannerPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("sort desc");

        return adBannerMapper.selectByExample(example);

    }
}
