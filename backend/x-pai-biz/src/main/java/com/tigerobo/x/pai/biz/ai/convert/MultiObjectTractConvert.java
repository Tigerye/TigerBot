package com.tigerobo.x.pai.biz.ai.convert;

import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.vo.AiMultiObjectTrackVo;
import com.tigerobo.x.pai.dal.ai.entity.AiMultiObjectTrackPo;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MultiObjectTractConvert {


    public static List<AiMultiObjectTrackVo> po2vos(List<AiMultiObjectTrackPo> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return null;
        }
        return pos.stream().map(MultiObjectTractConvert::po2vo).collect(Collectors.toList());

    }

    public static AiMultiObjectTrackVo po2vo(AiMultiObjectTrackPo po) {

        if (po == null) {
            return null;
        }

        AiMultiObjectTrackVo vo = new AiMultiObjectTrackVo();

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
