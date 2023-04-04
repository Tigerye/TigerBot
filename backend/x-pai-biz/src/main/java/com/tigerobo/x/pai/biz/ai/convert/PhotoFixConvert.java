package com.tigerobo.x.pai.biz.ai.convert;

import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.vo.PhotoFixVo;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.biz.interact.InteractVo;
import com.tigerobo.x.pai.dal.ai.entity.AiPhotoFixPo;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PhotoFixConvert {


    public static List<PhotoFixVo> po2vos(List<AiPhotoFixPo> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return null;
        }
        return pos.stream().map(PhotoFixConvert::po2vo).collect(Collectors.toList());

    }

    public static PhotoFixVo po2vo(AiPhotoFixPo po) {

        if (po == null) {
            return null;
        }

        PhotoFixVo vo = new PhotoFixVo();

        vo.setId(po.getId());

        vo.setReqId(po.getReqId());
        vo.setUserId(po.getUserId());

        vo.setInputPhoto(po.getInputPhoto());


        vo.setCreateTime(po.getCreateTime());


        vo.setProcessStatus(po.getProcessStatus());

        vo.setProcessStatusName(AiArtImageProcessEnum.getStatusName(po.getProcessStatus()));

        vo.setMsg(po.getMsg());

        vo.setOutputPhoto(po.getOutputPhoto());
        InteractVo interactVo = new InteractVo();
        interactVo.setBizType(BusinessEnum.PHOTO_FIX.getType());

        interactVo.setBizId(String.valueOf(po.getId()));

        vo.setInteract(interactVo);

        vo.setHeight(po.getHeight());
        vo.setWidth(po.getWidth());

        vo.setAppendColor(po.getAppendColor()==1);

        vo.setCompressOutputPhoto(po.getCompressOutputPhoto());

        return vo;
    }

}
