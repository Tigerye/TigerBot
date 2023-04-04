package com.tigerobo.x.pai.biz.aml.convert;

import com.tigerobo.x.pai.api.aml.dto.AmlModelDto;
import com.tigerobo.x.pai.api.aml.enums.ModelServiceStatusEnum;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.biz.utils.AmlDemoUtil;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AmlModelConverter {


    public static List<AmlModelDto> po2dtoList(List<AmlModelDo> poList){

        if (CollectionUtils.isEmpty(poList)){
            return new ArrayList<>();
        }
        return poList.stream().map(AmlModelConverter::po2dto).collect(Collectors.toList());
    }

    public static AmlModelDto po2dto(AmlModelDo po){

        if (po == null){
            return null;
        }
        AmlModelDto dto = new AmlModelDto();

        dto.setModelId(po.getId());
        dto.setAmlId(po.getAmlId());
        dto.setDataId(po.getDataId());
        dto.setBaseModelId(po.getBaseModelId());
        dto.setStatus(po.getStatus());
        dto.setName(po.getName());

        dto.setPrecision(po.getPrecision());
        dto.setRecall(po.getRecall());
        dto.setAvgPrecision(po.getAvgPrecision());


        dto.setStyle(po.getStyle());
        dto.setServiceStatus(po.getServiceStatus());

        ModelServiceStatusEnum serviceStatusEnum = ModelServiceStatusEnum.getByStatus(po.getServiceStatus());
        if (serviceStatusEnum!=null){
            dto.setServiceStatusName(serviceStatusEnum.getName());
        }

        dto.setModelFinishTime(po.getModelFinishTime());
        dto.setCreateTime(po.getCreateTime());
        dto.setUpdateTime(po.getUpdateTime());

        dto.setTotalEpoch(po.getTotalEpoch());

        if (po.getEpoch()!=null&&po.getTotalEpoch()!=null&&
                po.getEpoch()>0&&po.getTotalEpoch()>0
                && po.getEpoch().compareTo(po.getTotalEpoch())>=0
                &&!AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(dto.getStatus())){
            dto.setEpoch(po.getTotalEpoch()-1);
        }else {
            dto.setEpoch(po.getEpoch());
        }

        if (AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(dto.getStatus())){
            String demo = po.getDemo();

            API api = new API();

            api.setApiStyle(po.getStyle());
            api.setApiKey(String.valueOf(po.getId()));
            Map<String, Object> apiDemo = AmlDemoUtil.buildTextLabelDemo(demo);
//            api.setDemo(apiDemo);
            api.setApiDemo(apiDemo);
            dto.setApi(api);
        }
        dto.setErrMsg(po.getErrMsg());

        dto.setParentModelId(po.getParentModelId());

        return dto;
    }
}
