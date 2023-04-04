package com.tigerobo.x.pai.biz.aml.convert;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.aml.dto.AmlDatasetDto;
import com.tigerobo.x.pai.api.dto.FileData;
import com.tigerobo.x.pai.dal.aml.entity.AmlDatasetDo;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AmlDatasetConverter {


    public static List<AmlDatasetDto> po2dtoList(List<AmlDatasetDo> poList){
        if (CollectionUtils.isEmpty(poList)){
            return new ArrayList<>();
        }

        return poList.stream().map(AmlDatasetConverter::po2dto).collect(Collectors.toList());
    }

    public static AmlDatasetDto po2dto(AmlDatasetDo po){

        AmlDatasetDto dto = new AmlDatasetDto();

        dto.setDatasetId(po.getId());
        dto.setAllItemCount(po.getAllItemCount());
        dto.setBaseModelId(po.getBaseModelId());

        if (!StringUtils.isEmpty(po.getPath())){
            List<FileData> fileData = JSON.parseArray(po.getPath(), FileData.class);
            dto.setDatasetFileList(fileData);
        }

        dto.setFileType(po.getFileType());
        dto.setStatus(po.getStatus());

        dto.setAllItemCount(po.getAllItemCount());
        dto.setLabeledCount(po.getLabeledCount());
        dto.setTrainCount(po.getTrainCount());
        dto.setValidationCount(po.getValidationCount());
        dto.setTestCount(po.getTestCount());
        dto.setUnlabeledCount(po.getUnlabeledCount());
        return dto;
    }
}
