package com.tigerobo.x.pai.biz.utils;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AmlDemoUtil {

    public static Map<String,Object> buildTextLabel(String demo){
        if (StringUtils.isEmpty(demo)){
            return null;
        }
        Map<String,Object> map = new HashMap<>();

        map.put("texts", Arrays.asList(demo));
        return map;
    }

    public static Map<String,Object> buildTextLabelDemo(String demo){
        if (StringUtils.isEmpty(demo)){
            return null;
        }
        Map<String,Object> map = new HashMap<>();

        map.put("text", demo);
        return map;
    }


    public static Map<String,Object> buildTextLabelApiDemo(String demo){
        if (StringUtils.isEmpty(demo)){
            return null;
        }
        Map<String,Object> map = new HashMap<>();

        map.put("text", demo);
        return map;
    }
}
