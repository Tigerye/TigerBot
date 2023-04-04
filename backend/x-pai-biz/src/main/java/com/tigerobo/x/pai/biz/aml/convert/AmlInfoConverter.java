package com.tigerobo.x.pai.biz.aml.convert;

import com.tigerobo.x.pai.api.aml.dto.AmlInfoDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlCreateDto;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.dal.aml.entity.AmlInfoDo;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AmlInfoConverter {

    public static AmlInfoDo create2do(AmlCreateDto createDto){
        if (createDto == null){
            return null;
        }
        AmlInfoDo infoDo = new AmlInfoDo();
        infoDo.setBaseModelId(createDto.getBaseModelId());
        infoDo.setName(createDto.getName());
        infoDo.setCreateBy(createDto.getCreateBy());
        infoDo.setUpdateBy(createDto.getCreateBy());
        infoDo.setStatus(new Byte("0"));
        infoDo.setParentModelId(createDto.getParentModelId());
        infoDo.setTrainType(createDto.getTrainType());
        return infoDo;
    }
    public static List<AmlInfoDto> do2dtoList(List<AmlInfoDo> poList){
        if (CollectionUtils.isEmpty(poList)){
            return new ArrayList<>();
        }

        return poList.stream().map(po->do2dto(po)).collect(Collectors.toList());

    }
    public static AmlInfoDto do2dto(AmlInfoDo po){

        AmlInfoDto dto = new AmlInfoDto();

        dto.setId(po.getId());
        dto.setBaseModelId(po.getBaseModelId());
        dto.setCurrentDataId(po.getCurrentDataId());
        dto.setCurrentModelId(po.getCurrentModelId());
        Byte status = po.getStatus();
        AmlStatusEnum amlStatusEnum = AmlStatusEnum.getByStatus(status);
        String statusShowName = "未知";

        if (amlStatusEnum!=null){
            statusShowName = amlStatusEnum.getShowName()==null?amlStatusEnum.getName():amlStatusEnum.getShowName();
        }


        dto.setStatus(status);
        dto.setStatusName(statusShowName);
//        dto.setStatusShowName(statusShowName);
        dto.setName(po.getName());

        dto.setHide(po.getHide());
        dto.setCreateTime(po.getCreateTime());

        dto.setParentModelId(po.getParentModelId());
        dto.setTrainType(po.getTrainType());

        dto.setMsg(po.getMsg());
        return dto;
    }
}
