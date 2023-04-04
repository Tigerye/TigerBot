package com.tigerobo.x.pai.biz.lake;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LakeAbstractService {

    @Value("${pai.lake.abstract.url}")
    String abstractUrl;

    public String getAlgoletAbstract(List<String> contentList){

        if (CollectionUtils.isEmpty(contentList)){
            return "";
        }
        String collect = contentList.stream().filter(org.apache.commons.lang3.StringUtils::isNotBlank).collect(Collectors.joining(" "));
        if (collect.length()<50){
            return collect;
        }

        String anAbstract = getAbstract(collect);

        return anAbstract;
    }
    public String getAbstract(String content){
        if (StringUtils.isEmpty(content)){
            return "";
        }
        Map<String,Object> data = new HashMap<>();


        data.put("text_list", Arrays.asList(content));

        String post = RestUtil.post(abstractUrl, data);

        if (StringUtils.isEmpty(post)){
            return null;
        }
        JSONArray result = JSON.parseObject(post).getJSONArray("result");

        if (result ==null||result.size()<=0){
            return null;
        }
        return result.getString(0);
    }
}
