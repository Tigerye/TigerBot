package com.tigerobo.x.pai.biz.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

public class JacksonUtil {
    static ObjectMapper deserializeObjectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
            .configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false)
            .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);

    public static String bean2Json(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T json2Bean(String json, Class<T> klass) {

        if(StringUtils.isEmpty(json)) {
            return null;
        }

        try {
            return deserializeObjectMapper.readValue(json, klass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
