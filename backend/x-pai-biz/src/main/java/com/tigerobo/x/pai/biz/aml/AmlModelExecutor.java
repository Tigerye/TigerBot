package com.tigerobo.x.pai.biz.aml;

import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.biz.lake.LakeInferService;
import com.tigerobo.x.pai.biz.serving.Executable;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Map;

@Data
public class AmlModelExecutor implements Executable {

    String name;
    LakeInferService lakeInferService;
    private String apiKey;

    private String apiStyle;
    private Map<String, Object> apiDemo;

    String modelUrl;

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String getApiUri() {
        return null;
    }

    @Override
    public API profileClean() {
        return null;
    }

    @Override
    public ApiDto getApiDto() {
        return null;
    }

    @Override
    public Object execute(Map<String, Object> params) {
        String s = lakeInferService.doInfer(apiKey, params,modelUrl);
        if (StringUtils.isEmpty(s)){
            return null;
        }else {
            return JSONObject.parseObject(s);
        }
    }


}
