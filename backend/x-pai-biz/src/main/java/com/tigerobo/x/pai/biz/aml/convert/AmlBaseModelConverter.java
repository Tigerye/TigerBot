package com.tigerobo.x.pai.biz.aml.convert;

import com.tigerobo.x.pai.api.aml.dto.AmlBaseModelDto;
import com.tigerobo.x.pai.dal.aml.entity.AmlBaseModelDo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AmlBaseModelConverter {

    public static AmlBaseModelDto do2dto(AmlBaseModelDo po){

        AmlBaseModelDto dto = new AmlBaseModelDto();
        dto.setIntro(po.getIntro());
        dto.setId(po.getId());
        dto.setImg(po.getImg());
        dto.setStyle(po.getStyle());
        dto.setName(po.getName());
        return dto;
    }

    public static List<AmlBaseModelDto> do2dtoList(List<AmlBaseModelDo> poList){
        if (CollectionUtils.isEmpty(poList)){
            return new ArrayList<>();
        }
        return poList.stream().map(po->do2dto(po)).collect(Collectors.toList());
    }
}
