package com.tigerobo.x.pai.biz.ai.convert;

import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.vo.AiSpatioActionVo;
import com.tigerobo.x.pai.dal.ai.entity.AiSpatioActionPo;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SpatioActionConvert {


    public static List<AiSpatioActionVo> po2vos(List<AiSpatioActionPo> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return null;
        }
        return pos.stream().map(SpatioActionConvert::po2vo).collect(Collectors.toList());

    }

    public static AiSpatioActionVo po2vo(AiSpatioActionPo po) {

        if (po == null) {
            return null;
        }

        AiSpatioActionVo vo = new AiSpatioActionVo();

        vo.setId(po.getId());

        vo.setReqId(po.getReqId());
        vo.setUserId(po.getUserId());
        vo.setInputVideo(po.getInputVideo());

        vo.setCreateTime(po.getCreateTime());
        vo.setDealTime(po.getDealTime());

        vo.setProcessStatus(po.getProcessStatus());

        vo.setProcessStatusName(AiArtImageProcessEnum.getStatusName(po.getProcessStatus()));


        vo.setStatus(po.getStatus());

        vo.setMsg(po.getMsg());


        vo.setOutput(po.getOutput());


        if (po.getStatus() == 1) {
            vo.setPublishTime(po.getPublishTime());
        }

        vo.setTitle(po.getTitle());
        vo.setDesc(po.getDesc());

        return vo;
    }

}
