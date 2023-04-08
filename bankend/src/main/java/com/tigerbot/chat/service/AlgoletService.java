package com.tigerbot.chat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerbot.chat.api.dto.ModelLabel;
import com.tigerbot.chat.api.request.AiArtImageGenerateReq;
import com.tigerbot.chat.util.HttpReqUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AlgoletService {

    @Value("${alg.user.appId}")
    private String appId;

    @Value("${alg.user.accessToken}")
    private String accessToken;
    @Value("${alg.model.contentAudit.apiKey}")
    private String auditApiKey;

    @Value("${alg.model.artImage.apiKey}")
    private String artImageApiKey;

    public ModelLabel getAuditResult(String text) {

        final String url = getUrl(auditApiKey);
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);

        final JSONObject data = doCall(JSON.toJSONString(map), url);
        if (data == null) {
            return null;
        }
        JSONArray result = data.getJSONArray("result");
        if (result == null||result.isEmpty()) {
            return null;
        }
        return result.getObject(0, ModelLabel.class);
    }

    public Long imgProduceReq(String text){
        final String url = getUrl(artImageApiKey);
        AiArtImageGenerateReq req = buildImgReq(text);
        final JSONObject data = doCall(JSON.toJSONString(req), url);

        if (data == null){
            return null;
        }
        final JSONObject result = data.getJSONObject("result");
        if (result == null){
            return null;
        }
        return result.getLong("reqId");
    }


    private AiArtImageGenerateReq buildImgReq(String text) {
        AiArtImageGenerateReq req = new AiArtImageGenerateReq();
        AiArtImageGenerateReq.ArtImageParams params = new AiArtImageGenerateReq.ArtImageParams();
        params.setText(text);
        req.setInputParam(Arrays.asList(params));
        req.setPromptWeight(9f);
        req.setImageStrength(0.8f);
        req.setSteps(50);
        req.setStyleType("stable");
        req.setSizeId(168);
        req.setNIter(1);
        req.setCoinTotal(1);

        req.setSeed((int)(System.currentTimeMillis()/1000));
        req.setWidth(512);
        req.setHeight(512);

        req.setModelVersion("v2.1");
        return req;
    }

    public JSONObject doCall(String json, String url) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        final String resp = HttpReqUtil.reqPost(url,json, 3000);
        if (StringUtils.isBlank(resp)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(resp);
        return jsonObject.getJSONObject("data");
    }


    public String getUrl(String apiKey){
        return  "http://pai.tigerobo.com/x-pai-serving/invoke?"
                + "appId="+appId
                +"&apiKey=" + apiKey
                + "&accessToken="+accessToken;
    }

}
