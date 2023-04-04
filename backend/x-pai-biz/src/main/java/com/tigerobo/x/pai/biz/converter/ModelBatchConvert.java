package com.tigerobo.x.pai.biz.converter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.enums.BatchTaskEnum;
import com.tigerobo.x.pai.api.enums.JobStatusEnum;
import com.tigerobo.x.pai.api.vo.model.ModelBatchTaskVo;
import com.tigerobo.x.pai.biz.dto.BatchCallDto;
import com.tigerobo.x.pai.dal.biz.entity.offline.ModelBatchTaskPo;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ModelBatchConvert {
    public static List<ModelBatchTaskVo> convert(List<ModelBatchTaskPo> pos){

        if (CollectionUtils.isEmpty(pos)){
            return new ArrayList<>();
        }
        return pos.stream().map(ModelBatchConvert::convert).collect(Collectors.toList());
    }
    public static ModelBatchTaskVo convert(ModelBatchTaskPo po){

        if (po == null){
            return null;
        }
        ModelBatchTaskVo vo = new ModelBatchTaskVo();
        vo.setId(po.getId());
        vo.setReqId(po.getReqId());
        vo.setUserId(po.getUserId());
        vo.setModelId(po.getBizId());
        vo.setModelName(po.getBizName());
        vo.setInputPath(po.getInputPath());
        vo.setOutPath(po.getOutPath());
        vo.setStatus(po.getStatus());
        vo.setDealNum(po.getDealNum());
        vo.setCreateTime(po.getCreateTime());
        vo.setErrMsg(po.getErrMsg());
        vo.setFileName(po.getFileName());
        vo.setTotalNum(po.getTotalNum());

        BatchTaskEnum statusEnum = BatchTaskEnum.getByStatus(po.getStatus());

        if (statusEnum!=null){
            vo.setStatusName(statusEnum.getText());
        }else {
            vo.setStatusName("");
        }

        String result = po.getResult();
        if (po.getStatus()!=null&&po.getStatus().equals(1)&&StringUtils.isNotBlank(result)){
            BatchCallDto batchCallDto = JSON.parseObject(result, BatchCallDto.class);
            vo.setResult(batchCallDto);
        }
        return vo;
    }
}
