package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import com.tigerobo.x.pai.dal.biz.entity.model.ModelCategoryPo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ModelCategoryConvert {

    public static ModelCategoryDto po2dto(ModelCategoryPo po){
        if (po == null){
            return null;
        }
        ModelCategoryDto dto = new ModelCategoryDto();

        BeanUtils.copyProperties(po,dto);
        return dto;
    }

    public static List<ModelCategoryDto> po2dtos(List<ModelCategoryPo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return new ArrayList<>();
        }
        return pos.stream().map(ModelCategoryConvert::po2dto).collect(Collectors.toList());
    }
}
