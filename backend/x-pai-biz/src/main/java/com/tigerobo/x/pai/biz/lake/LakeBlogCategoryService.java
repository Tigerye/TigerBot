package com.tigerobo.x.pai.biz.lake;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.dto.lake.LakeLabelDto;
import com.tigerobo.x.pai.api.dto.lake.LakeLabelResult;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LakeBlogCategoryService {

    @Value("${pai.blog.category.url}")
    private String aiCategoryUrl;// ="http://gbox4.aigauss.com:9506/infer";

    @Value("${pai.blog.category.threshold:0.6}")
    String categoryThreshold;
    public List<String> callCategory(Integer id, String text, List<String> candidateLabels){
        if (StringUtils.isBlank(text)||CollectionUtils.isEmpty(candidateLabels)){
            return null;
        }
        long start = System.currentTimeMillis();

        Map<String,Object> map = new HashMap<>();
        map.put("text_list",Arrays.asList(text));
        map.put("candidate_labels",candidateLabels);
        String post = RestUtil.post(aiCategoryUrl, map);

        long delta = System.currentTimeMillis() - start;
        if (delta>1000){
            log.info("category-time:{}ms", delta);
        }
        if (StringUtils.isNotBlank(post)){
            return getLabels(id,post);
        }
        return null;
    }

    private List<String> getLabels(Integer id,String json){
        if (StringUtils.isBlank(json)){
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject data = jsonObject.getJSONObject("data");
        LakeLabelResult labelResult = null;
        if (data==null){
            labelResult = JSON.parseObject(json, LakeLabelResult.class);
        }else {
            labelResult = jsonObject.getObject("data",LakeLabelResult.class);
        }
        if (labelResult == null){
            throw new IllegalArgumentException("id="+id+","+"解析标签异常");
        }
        if (labelResult.getStatus()!=0){
            log.error("获取分类标签异常{}",labelResult.getMsg());
        }

        List<List<LakeLabelDto>> result = labelResult.getResult();
        if (!CollectionUtils.isEmpty(result)){
            List<LakeLabelDto> lakeLabelDtos = result.get(0);

            if (CollectionUtils.isEmpty(lakeLabelDtos)){
                return null;
            }

//            System.out.println(JSON.toJSONString(lakeLabelDtos));
//            System.out.println(lakeLabelDtos.size());
            BigDecimal threshold = new BigDecimal(categoryThreshold);
            List<String> collect = lakeLabelDtos.stream()
                    .filter(label -> label.getScore() != null && label.getScore().compareTo(threshold) >= 0).limit(5)
                    .map(label -> label.getLabel()).collect(Collectors.toList());
//            System.out.println("coll-"+collect.size());
            return collect;
        }
        return null;
    }

}
