package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.dto.UserCommitSiteDto;
import com.tigerobo.x.pai.api.dto.admin.PubSiteDto;
import com.tigerobo.x.pai.api.enums.CommitSiteStatusEnum;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubSitePo;
import org.springframework.beans.BeanUtils;

import java.util.Collection;

public class SiteConvert {

    public static PubSiteVo convert(PubSitePo po, Collection<Integer> followCollection){

        if (po == null){
            return null;
        }
        PubSiteVo vo = new PubSiteVo();

        vo.setLogoOss(po.getLogoOss());
        vo.setId(po.getId());
        vo.setName(po.getName());
        vo.setIntro(po.getIntro());
        if (followCollection!=null&& followCollection.contains(po.getId())){
            vo.setFollow(followCollection.contains(po.getId()));
        }

        vo.setVip(po.getVip());

        return vo;
    }
    public static PubSiteDto po2dto(PubSitePo po){
        PubSiteDto dto=new PubSiteDto();
        BeanUtils.copyProperties(po,dto);
        return dto;
    }

}
