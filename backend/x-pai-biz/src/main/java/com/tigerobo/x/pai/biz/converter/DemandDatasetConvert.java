package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.dto.DemandDataset;
import com.tigerobo.x.pai.dal.biz.entity.DemandDatasetPo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DemandDatasetConvert {

    public static DemandDatasetPo dto2po(DemandDataset dto){
        DemandDatasetPo po = new DemandDatasetPo();
        po.setDemandUuid(dto.getDemandUuid());
        po.setFilePath(dto.getFilePath());
        po.setName(dto.getName());
        po.setCreateBy(dto.getCreateBy());
        po.setUpdateBy(dto.getUpdateBy());
        return po;
    }

    public static List<DemandDatasetPo> dto2poList(List<DemandDataset> poList){
        if (CollectionUtils.isEmpty(poList)){
            return new ArrayList<>();
        }
        return poList.stream().map(dto->dto2po(dto)).collect(Collectors.toList());
    }


    public static DemandDataset po2dto(DemandDatasetPo po){

        return DemandDataset.builder()
                .demandUuid(po.getDemandUuid())
                .filePath(po.getFilePath())
                .name(po.getName())
                .createTIme(po.getCreateTime())
                .createBy(po.getCreateBy())
                .updateBy(po.getUpdateBy())
                .build();
    }

    public static List<DemandDataset> po2dtoList(List<DemandDatasetPo> poList){
        if (CollectionUtils.isEmpty(poList)){
            return new ArrayList<>();
        }
        return poList.stream().map(po->po2dto(po)).collect(Collectors.toList());
    }
}
