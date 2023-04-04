package com.tigerobo.x.pai.biz.ai.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.AiArtImageGenerateReq;
import com.tigerobo.x.pai.api.ai.vo.AiArtImageVo;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.dto.admin.ai.ArtImageDto;
import com.tigerobo.x.pai.api.enums.ArtImageType;
import com.tigerobo.x.pai.api.vo.biz.interact.InteractVo;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ArtImageConvert {
    public static List<AiArtImageVo> po2vos(List<AiArtImagePo> pos,final boolean isGuest) {
        if (CollectionUtils.isEmpty(pos)) {
            return null;
        }
        return pos.stream().map(po->po2vo(po,isGuest)).collect(Collectors.toList());
    }
    public static List<AiArtImageVo> po2vos(List<AiArtImagePo> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return null;
        }
        return pos.stream().map(ArtImageConvert::po2vo).collect(Collectors.toList());

    }

    public static List<ArtImageDto> po2dtos(List<AiArtImagePo> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return null;
        }
        return pos.stream().map(ArtImageConvert::po2dto).collect(Collectors.toList());

    }


    public static ArtImageDto po2dto(AiArtImagePo po) {

        if (po == null) {
            return null;
        }

        ArtImageDto dto = new ArtImageDto();

        dto.setId(po.getId());

        dto.setReqId(po.getReqId());
        dto.setUserId(po.getUserId());

        dto.setInputImage(po.getInputImage());
        dto.setImageProgress(po.getImageProgress());

        dto.setCreateTime(po.getCreateTime());
        dto.setDealTime(po.getDealTime());

        dto.setText(po.getText());

        dto.setProcessStatus(po.getProcessStatus());

        dto.setProcessStatusName(AiArtImageProcessEnum.getStatusName(po.getProcessStatus()));

        dto.setTotalProgress(po.getTotalProgress());
        dto.setStatus(po.getStatus());

        dto.setMsg(po.getMsg());

        String modifiers = po.getModifiers();

        if (StringUtils.isNotBlank(modifiers)) {
            List<String> list = JSONArray.parseArray(modifiers, String.class);
            dto.setModifiers(list);
        } else {
            dto.setModifiers(new ArrayList<>());
        }

        dto.setProgress(po.getProgress());


        if (po.getStatus() == 1) {
            dto.setPublishTime(po.getPublishTime());
        }

        dto.setTitle(po.getTitle());
        dto.setDesc(po.getDesc());
        dto.setOutputImage(po.getOutputImage());

        if (AiArtImageProcessEnum.SUCCESS.getStatus().equals(po.getProcessStatus()) && !StringUtils.isEmpty(po.getProgressImages())) {
            String progressImages = po.getProgressImages();
            JSONObject jsonObject = JSON.parseObject(progressImages);

            List<AiArtImageVo.ProgressImage> progressImageList = new ArrayList<>();
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                Integer progress = Integer.parseInt(key);
                String url = jsonObject.getString(key);

                int rate = new BigDecimal(progress * 100).divide(new BigDecimal(po.getTotalProgress()), RoundingMode.HALF_DOWN).intValue();
                progressImageList.add(AiArtImageVo.ProgressImage.builder().progress(progress).rate(rate).imageUrl(url).build());
            }
            progressImageList.sort(Comparator.comparing(AiArtImageVo.ProgressImage::getRate));
            dto.setProgressImages(progressImageList);
        }

        dto.setUseFree(po.getUseFree());
        dto.setCoinStatus(po.getCoinStatus());
        dto.setCoinTotal(po.getCoinTotal());


        return dto;
    }
    public static AiArtImageVo po2vo(AiArtImagePo po,boolean isGuest) {
        final AiArtImageVo vo = po2vo(po);
        if(vo == null){
            return null;
        }
        if (isGuest){
            vo.setCoinTotal(null);

            if (vo.getHide()!=null&&vo.getHide()){

                vo.setInputParam(null);
                vo.setText("");
                vo.setInputImage("");
                vo.setModifiers(null);
                vo.setProgressImages(new ArrayList<>());
            }
        }
        return vo;
    }
    public static AiArtImageVo po2vo(AiArtImagePo po) {

        if (po == null) {
            return null;
        }

        AiArtImageVo vo = new AiArtImageVo();

        final String inputParam = po.getInputParam();
        if (StringUtils.isNotBlank(inputParam)){
            final List<AiArtImageGenerateReq.ArtImageParams> artImageParams = JSON.parseArray(inputParam, AiArtImageGenerateReq.ArtImageParams.class);
            vo.setInputParam(artImageParams);
        }else {
            AiArtImageGenerateReq.ArtImageParams params = new AiArtImageGenerateReq.ArtImageParams();
            params.setText(po.getText());
        }


        vo.setId(po.getId());

        vo.setReqId(po.getReqId());
        vo.setUserId(po.getUserId());

        vo.setInputImage(po.getInputImage());
        vo.setImageProgress(po.getImageProgress());

        vo.setCreateTime(po.getCreateTime());
        vo.setDealTime(po.getDealTime());

        vo.setText(po.getText());

        vo.setProcessStatus(po.getProcessStatus());

        vo.setProcessStatusName(AiArtImageProcessEnum.getStatusName(po.getProcessStatus()));

        vo.setTotalProgress(po.getTotalProgress());
        vo.setStatus(po.getStatus());

        vo.setMsg(po.getMsg());

        final Float promptWeight = po.getPromptWeight();
        if (promptWeight!=null&&promptWeight>0){
            vo.setPromptWeight(promptWeight);
        }

        final Float imageStrength = po.getImageStrength();
        if (imageStrength!=null&&imageStrength>0){
            vo.setImageStrength(imageStrength);
        }

        final Integer steps = po.getSteps();
        if (steps!=null&&steps>0){
            vo.setSteps(steps);
        }

        String modifiers = po.getModifiers();

        if (StringUtils.isNotBlank(modifiers)) {
            List<String> list = JSONArray.parseArray(modifiers, String.class);
            vo.setModifiers(list);
        } else {
            vo.setModifiers(new ArrayList<>());
        }

        initProcessData(po, vo);

        if (po.getStatus() == 1) {
            vo.setPublishTime(po.getPublishTime());
        }

        vo.setTitle(po.getTitle());
        vo.setDesc(po.getDesc());

        final Integer styleType = po.getStyleType();

        final ArtImageType styleTypeEnum = ArtImageType.getByType(styleType);
        if (styleTypeEnum !=null){
            vo.setStyleType(styleTypeEnum.toString().toLowerCase());
        }

        final Integer width = po.getWidth();
        vo.setWidth(width);
        vo.setHeight(po.getHeight());

        if (AiArtImageProcessEnum.SUCCESS.getStatus().equals(po.getProcessStatus()) && !StringUtils.isEmpty(po.getProgressImages())) {
            String progressImages = po.getProgressImages();
            JSONObject jsonObject = JSON.parseObject(progressImages);

            List<AiArtImageVo.ProgressImage> progressImageList = new ArrayList<>();
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                Integer progress = Integer.parseInt(key);
                String url = jsonObject.getString(key);

                int rate = new BigDecimal(progress * 100).divide(new BigDecimal(po.getTotalProgress()), RoundingMode.HALF_DOWN).intValue();
                progressImageList.add(AiArtImageVo.ProgressImage.builder().progress(progress).rate(rate).imageUrl(url).build());
            }
            progressImageList.sort(Comparator.comparing(AiArtImageVo.ProgressImage::getRate));
            vo.setProgressImages(progressImageList);

            final String iterImages = po.getIterImages();
            if (StringUtils.isNotBlank(iterImages)){

                final List<String> array = JSONArray.parseArray(iterImages,String.class);
                vo.setIterImages(array);
            }
            vo.setGridImage(po.getGridImage());
        }
        vo.setUseFree(po.getUseFree());
        vo.setCoinStatus(po.getCoinStatus());
        vo.setCoinTotal(po.getCoinTotal());

        vo.setNIter(po.getNIter());
        vo.setSeed(po.getSeed());
        vo.setModelVersion(po.getModelVersion());
        vo.setHide(po.getHide());
        return vo;
    }

    private static void initProcessData(AiArtImagePo po, AiArtImageVo vo) {
        String outputImage = po.getOutputImage();
        if (AiArtImageProcessEnum.ON_PROCESS.getStatus().equals(po.getProcessStatus())) {
            Integer progress = po.getProgress();
            Integer totalProgress = po.getTotalProgress();
            if (totalProgress == null || totalProgress == 0) {
                vo.setProgressRate(0);
            } else {
                int progressRate = progress * 100 / totalProgress;
                vo.setProgressRate(progressRate);
            }
            if (StringUtils.isNotBlank(outputImage)&& vo.getProgressRate()!=null){
                if (outputImage.contains("?")){
                    outputImage +="&v="+vo.getProgressRate();
                }else {
                    outputImage += "?v="+ vo.getProgressRate();
                }
            }
        }
        vo.setOutputImage(outputImage);
    }
}
