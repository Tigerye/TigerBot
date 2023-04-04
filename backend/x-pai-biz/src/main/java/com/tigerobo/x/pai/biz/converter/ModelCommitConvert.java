package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.dto.ModelCommitDto;
import com.tigerobo.x.pai.dal.biz.entity.ModelCommitPo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ModelCommitConvert {

    public static ModelCommitPo dto2po(ModelCommitDto dto){
        if (dto == null){
            return null;
        }

        ModelCommitPo po = new ModelCommitPo();

        BeanUtils.copyProperties(dto,po);
        return po;
    }

    public static List<ModelCommitDto> po2dto(List<ModelCommitPo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.stream().map(ModelCommitConvert::po2dto).collect(Collectors.toList());
    }

    public static ModelCommitDto po2dto(ModelCommitPo po){
        if (po == null){
            return null;
        }
        ModelCommitDto dto = new ModelCommitDto();

        BeanUtils.copyProperties(po,dto);
        return dto;

    }
}
