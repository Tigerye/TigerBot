package com.tigerobo.x.pai.biz.aml.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AmlLakeService {

    @Value(value = "http://gbox8.aigauss.com:9898/infer")
    private String modelPathUrl;

    @Autowired
    private EnvService envService;

    public String getModelPath(Integer modelId, String modelArea) {

        String envPrefix = envService.getEnvPrefix();
        String reqModelId = envPrefix + modelId;

        Map<String, Object> map = new HashMap();
        map.put("model_name", reqModelId);
        map.put("model_area", modelArea);
        String post = RestUtil.post(modelPathUrl, map);

        Validate.isTrue(StringUtils.isNotBlank(post), "查看模型路径失败");

        JSONObject jsonObject = JSON.parseObject(post);
        return jsonObject.getString("model_path");
    }

}
