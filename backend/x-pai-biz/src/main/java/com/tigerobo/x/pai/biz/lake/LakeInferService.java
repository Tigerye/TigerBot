package com.tigerobo.x.pai.biz.lake;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
public class LakeInferService {
    @Value("${pai.env.lake.infer.base.url:http://gbox8.aigauss.com:9595}")
    private String inferUrlBase;
//    @Value("${pai.lake.infer.url}")
//    private String inferUrl = "http://gbox8.aigauss.com:9595/infer/content";
    @Value("${pai.env.aml.prefix}")
    private String envPrefix;


    public String infer(String modelName, Map<String, Object> params,String url){
        if (params==null||params.isEmpty()){
            return null;
        }
        Map<String,Object> reqMap = new HashMap<>();

        Object text_list = params.get("text_list");
        if (text_list==null){
            Object text = params.get("text");
            if (text!=null){
                reqMap.put("text_list",Lists.newArrayList(text));
            }
        }else {
            reqMap.put("text_list",text_list);
        }

        return doInfer(modelName, reqMap, url);
    }

    public String parseScore(String resultStr){


        if (StringUtils.isBlank(resultStr)){
            return resultStr;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(resultStr);

            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.size(); i++) {

                JSONArray subArray = result.getJSONArray(i);

                for (int j = 0; j < subArray.size(); j++) {
                    JSONObject subJson = subArray.getJSONObject(j);
                    BigDecimal score = subJson.getBigDecimal("score");
                    if (score != null) {
                        BigDecimal bigDecimal = score.setScale(8, RoundingMode.DOWN);
                        subJson.put("score", bigDecimal);
                    }
                }
            }
            return JSON.toJSONString(jsonObject);
        }catch (Exception ex){
            log.error("parse-score:{}",resultStr,ex);
        }
        return resultStr;
    }

    /**
     * 1-api
     * 2-批量预测
     * @param modelName
     * @param params
     * @return
     */
    public String doInfer(String modelName, Map<String, Object> params,String url) {
        String s = innerInfer(modelName, params, url);
        return parseScore(s);
    }
    public String innerInfer(String modelName, Map<String, Object> params,String url) {
        if (StringUtils.isBlank(url)){
            url = inferUrlBase+"/infer";
        }else {
            url +="/infer";
        }
        Map<String, Object> data = new HashMap<>();
        if (!CollectionUtils.isEmpty(params)) {
            data.putAll(params);
        }
        String modelNameCall = envPrefix +modelName;
        data.put("model_name", modelNameCall);

//        return OkHttpUtil.jsonPost(inferUrl, data);
        return RestUtil.post(url,data);
    }


}
