package com.tigerobo.x.pai.biz.ai.art.image;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.req.AiArtImageGenerateReq;
import com.tigerobo.x.pai.biz.utils.NasFileUtil;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.dal.ai.dao.AiParamDictDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import com.tigerobo.x.pai.dal.ai.entity.AiParamDictPo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArtImageLakeService {
    @Autowired
    private AiParamDictDao aiParamDictDao;


    @Value("${pai.artImage.disco.url:http://gbox9.aigauss.com:9100/infer}")
    private String discoUrl;
    @Value("${pai.artImage.stable.url:http://gbox9.aigauss.com:9300/infer}")
    private String stableUrl;

    @Value("#{${pai.artImage.replaceMap}}")
    Map<String, String> replaceMap;

    public Map<String,String> getReplaceMap(){
        return replaceMap;
    }
    public void reqStable(AiArtImagePo reqPo, String imageFilePath,
                         String outputPath) {

        final String inputParam = reqPo.getInputParam();

        Validate.isTrue(StringUtils.isNotBlank(inputParam),"入参为空");

        final List<AiArtImageGenerateReq.ArtImageParams> artImageParams = JSON.parseArray(inputParam,
                AiArtImageGenerateReq.ArtImageParams.class);

        Validate.isTrue(artImageParams!=null&&artImageParams.size()>0,"入参为空");
        initNas(outputPath);

        StableReq req = new StableReq();
        final AiArtImageGenerateReq.ArtImageParams params = artImageParams.get(0);
        final String cleanText = getCleanText(params.getText());
        req.setText(cleanText);
        req.setModifiers(params.getModifiers());
        req.imagePath = imageFilePath;
        req.outputPath = outputPath;
        req.width = reqPo.getWidth();
        req.height = reqPo.getHeight();

        final Integer nIter = reqPo.getNIter();
        if (nIter!=null&&nIter>0){
            req.nIter = nIter;
        }

        req.seed = reqPo.getSeed();

        final Float imageStrength = reqPo.getImageStrength();
        if (imageStrength!=null&&imageStrength>0){
            req.image_strength = imageStrength;
        }

        final Float promptWeight = reqPo.getPromptWeight();
        if (promptWeight!=null&&promptWeight>0){
            req.prompt_weight = promptWeight;
        }

        final Integer steps = reqPo.getSteps();
        if (steps!=null&&steps>0){
            req.steps = steps;
        }

        if (StringUtils.isNotBlank(reqPo.getModelVersion())){
            req.version = reqPo.getModelVersion();
        }

        final LinkedMultiValueMap<String, Object> httpReqBody = getStableReqMap(req);
        String s = RestUtil.postWithFile(stableUrl, httpReqBody);
        dealResp(httpReqBody, s);
    }

    private String getCleanText(String text){
        if (CollectionUtils.isEmpty(replaceMap)){
            return text;
        }

        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            text = text.replaceAll(entry.getKey(),entry.getValue());
        }
        return text;
    }

    /**
     *
     * @param reqPo
     * @param imageFilePath
     * @param outputPath
     */
    public void reqDisco(AiArtImagePo reqPo, String imageFilePath,
                         String outputPath) {
        String inferUrl = discoUrl;
        final String inputParam = reqPo.getInputParam();

        Validate.isTrue(StringUtils.isNotBlank(inputParam),"入参为空");

        final List<AiArtImageGenerateReq.ArtImageParams> artImageParams = JSON.parseArray(inputParam,
                AiArtImageGenerateReq.ArtImageParams.class);

        Validate.isTrue(artImageParams!=null&&artImageParams.size()>0,"入参为空");


        DiscoReq req = new DiscoReq();
        req.params = artImageParams;
        req.imagePath = imageFilePath;
        req.outputPath = outputPath;
        req.width = reqPo.getWidth();
        req.height = reqPo.getHeight();

        LinkedMultiValueMap<String, Object> map = getDiscoReqMap(req);

        initNas(outputPath);
        String s = RestUtil.postWithFile(inferUrl, map);

        dealResp(map, s);
    }

    private void initNas(String outputPath) {
        NasFileUtil.delDir(outputPath);
        NasFileUtil.chmodDir(outputPath);
    }

    private LinkedMultiValueMap<String, Object> getDiscoReqMap(DiscoReq req) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        final FileSystemResource imageSource = getImageSource(req.imagePath);
        if (imageSource!=null){
            map.add("init_image", imageSource);
        }
        for (AiArtImageGenerateReq.ArtImageParams artImageParam : req.params) {
            String s = convert2ReqParam(artImageParam);
            map.add("text_prompt", s);
        }

        map.add("output_path", req.outputPath);
        map.add("width",req.width);
        map.add("height",req.height);
        return map;
    }

    private LinkedMultiValueMap<String, Object> getStableReqMap(StableReq req) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        final FileSystemResource imageSource = getImageSource(req.imagePath);
        if (imageSource!=null){
            map.add("init_image", imageSource);
        }

        final String modifiers = combineModifiers(req.getModifiers());

        map.add("text_prompt", req.text);
        map.add("modifiers",modifiers);

        map.add("output_path", req.outputPath);
        map.add("width",req.width);
        map.add("height",req.height);

        map.add("image_strength",req.getImage_strength());
        final Float prompt_weight = req.getPrompt_weight();
        if (prompt_weight!=null){
            final int i = prompt_weight.intValue();
            map.add("prompt_weight" , i);
        }

        map.add("steps",req.getSteps());
        map.add("n_iter",req.getNIter());
        map.add("seed",req.getSeed());
        if (StringUtils.isNotBlank(req.getVersion())){
            map.add("version",req.getVersion());
        }
        return map;
    }

    private FileSystemResource getImageSource(String imageFilePath){
        if (StringUtils.isBlank(imageFilePath)){
            return null;
        }
        return new FileSystemResource(imageFilePath);
    }

    private void dealResp(MultiValueMap<String, Object> map, String s) {
        if (StringUtils.isBlank(s)) {
            log.error("req:{}", JSON.toJSONString(map));
            throw new IllegalArgumentException("创建任务失败");
        }
        JSONObject jsonObject = JSON.parseObject(s);
        Integer status = jsonObject.getInteger("status");
        if (status == null || status != 0) {
            log.error("result：{}", s);

            String msg = jsonObject.getString("msg");
            if (StringUtils.isBlank(msg)) {
                msg = "模型调用失败";
            }
            throw new IllegalArgumentException(msg);
        }
    }


    private String convert2ReqParam(AiArtImageGenerateReq.ArtImageParams params){

        final String text = params.getText();

        Float weight = params.getWeight();
        Map<String,Object> data = new LinkedMap<>();

        data.put("text",text);
        final String modifiers = combineModifiers(params.getModifiers());
        data.put("modifiers",modifiers);
        if (weight==null){
            weight =1f;
        }
        data.put("weight",String.valueOf(weight));
        return JSON.toJSONString(data);
    }

    private String combineModifiers(List<String> modifiers){
        if (CollectionUtils.isEmpty(modifiers)){
            return "";
        }
        final List<String> realModifiers = getRealModifiers(modifiers);
        if (CollectionUtils.isEmpty(realModifiers)){
            return "";
        }
        return String.join(",",realModifiers);
    }


    private List<String> getRealModifiers(List<String> inputList){
        if (CollectionUtils.isEmpty(inputList)){
            return null;
        }
        final List<AiParamDictPo> pos = aiParamDictDao.getFromCache(2);

        if (pos==null||pos.isEmpty()){
            return inputList;
        }
        final Map<String, String> map = pos.stream()
                .collect(Collectors.toMap(AiParamDictPo::getName, AiParamDictPo::getText));

        final List<String> collect = inputList.stream()
                .map(in -> map.getOrDefault(in, in))
                .collect(Collectors.toList());
        return collect;
    }

    @Data
    public static class StableReq extends BaseReq{
        AiArtImageGenerateReq.ArtImageParams param;
        List<String> modifiers;
        String text;
        Float image_strength;
        Float prompt_weight;
        Integer steps;
        Integer nIter;
        Integer seed;
        String version;

    }


    private static class DiscoReq extends BaseReq{

    }

    @Data
    private static class BaseReq{
        String imagePath;
        String outputPath;
        Integer width;
        Integer height;
        List<AiArtImageGenerateReq.ArtImageParams> params;
    }
}
