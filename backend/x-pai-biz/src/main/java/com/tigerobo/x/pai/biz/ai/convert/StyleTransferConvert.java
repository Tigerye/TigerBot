package com.tigerobo.x.pai.biz.ai.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.vo.AiArtImageVo;
import com.tigerobo.x.pai.api.ai.vo.AiStyleTransferVo;
import com.tigerobo.x.pai.api.dto.admin.ai.ArtImageDto;
import com.tigerobo.x.pai.api.dto.admin.ai.StyleTransferDto;
import com.tigerobo.x.pai.dal.ai.entity.AiStyleTransferPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StyleTransferConvert {


    public static List<AiStyleTransferVo> po2vos(List<AiStyleTransferPo> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return null;
        }
        return pos.stream().map(StyleTransferConvert::po2vo).collect(Collectors.toList());

    }


    public static AiStyleTransferVo po2vo(AiStyleTransferPo po) {

        if (po == null) {
            return null;
        }

        AiStyleTransferVo vo = new AiStyleTransferVo();

        vo.setId(po.getId());

        vo.setReqId(po.getReqId());
        vo.setUserId(po.getUserId());

        vo.setContentImage(po.getContentImage());
        vo.setStyleImage(po.getStyleImage());

        vo.setImageProgress(po.getImageProgress());

        vo.setCreateTime(po.getCreateTime());
        vo.setDealTime(po.getDealTime());

        vo.setProcessStatus(po.getProcessStatus());

        vo.setProcessStatusName(AiArtImageProcessEnum.getStatusName(po.getProcessStatus()));

        vo.setTotalProgress(po.getTotalProgress());
        vo.setStatus(po.getStatus());

        vo.setMsg(po.getMsg());


        vo.setOutputImage(po.getOutputImage());


        if (po.getStatus() == 1) {
            vo.setPublishTime(po.getPublishTime());
        }

        vo.setTitle(po.getTitle());
        vo.setDesc(po.getDesc());

        if (AiArtImageProcessEnum.SUCCESS.getStatus().equals(po.getProcessStatus()) && !StringUtils.isEmpty(po.getProgressImages())) {
            String progressImages = po.getProgressImages();
            JSONObject jsonObject = JSON.parseObject(progressImages);

            List<AiStyleTransferVo.ProgressImage> progressImageList = new ArrayList<>();
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                Integer progress = Integer.parseInt(key);
                String url = jsonObject.getString(key);

                int rate = new BigDecimal(progress * 100).divide(new BigDecimal(po.getTotalProgress()), RoundingMode.HALF_DOWN).intValue();
                progressImageList.add(AiStyleTransferVo.ProgressImage.builder().progress(progress).rate(rate).imageUrl(url).build());
            }
            progressImageList.sort(Comparator.comparing(AiStyleTransferVo.ProgressImage::getRate));
            vo.setProgressImages(progressImageList);
        }
        return vo;
    }

}
