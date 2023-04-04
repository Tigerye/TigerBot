package com.tigerobo.x.pai.engine.auto.ml.util;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyStatisticDto;
import org.apache.commons.lang3.StringUtils;

public class AmlDataParseUtil {

    public static TextClassifyStatisticDto getTCS(String data){
        if (StringUtils.isBlank(data)){
            return null;
        }

        return JSON.parseObject(data,TextClassifyStatisticDto.class);
    }
}
