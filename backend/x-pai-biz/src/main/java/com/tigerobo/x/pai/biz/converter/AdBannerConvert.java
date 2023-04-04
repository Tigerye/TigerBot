package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.dto.ad.AdBannerDto;
import com.tigerobo.x.pai.dal.biz.entity.ad.AdBannerPo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class AdBannerConvert {


    public static AdBannerDto po2dto(AdBannerPo po){

        if ( po == null){
            return null;
        }
        AdBannerDto dto = new AdBannerDto();
        BeanUtils.copyProperties(po,dto);
        return dto;
    }


    public static List<AdBannerDto> po2dtos(List<AdBannerPo> poList){

        if (CollectionUtils.isEmpty(poList)){
            return null;
        }

        return poList.stream().map(AdBannerConvert::po2dto).collect(Collectors.toList());
    }

    public static AdBannerPo dto2po(AdBannerDto dto){
        if (dto == null){
            return null;
        }
        AdBannerPo po = new AdBannerPo();

        BeanUtils.copyProperties(dto,po);
        return po;
    }
}
