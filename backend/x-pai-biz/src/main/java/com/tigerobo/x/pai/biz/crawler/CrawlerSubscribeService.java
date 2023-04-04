package com.tigerobo.x.pai.biz.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.crawler.CrawlerBlog;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class CrawlerSubscribeService {
    private String url = "http://10.0.19.103:11800/rest/api/v1/rem/submit?app=c01&sign=e1fe4fc470b694994eba0f691c56a033";


    public Integer subscribe(String twitterUrl) {

        if (StringUtils.isBlank(twitterUrl)){
            return null;
        }
        Map<String, Object> params = new HashMap<>();

        params.put("type", "twitter");
        params.put("list", Arrays.asList(twitterUrl));

        String response = RestUtil.post(url, params);

        log.info("req,SubscribeUrl:{},resp:{}",twitterUrl,response);
        if (StringUtils.isEmpty(response)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(response);
        JSONObject data = jsonObject.getJSONObject("data");


        JSONObject add = data.getJSONObject("add");
        if (add!=null&&add.size()>0){

            for (Object value : add.values()) {
                if (value!=null&&String.valueOf(value).matches("\\d+")){
                    return Integer.parseInt(String.valueOf(value));
                }
            }
        }
        JSONObject exist = data.getJSONObject("exist");
        if (exist!=null&&exist.size()>0){

            for (Object value : exist.values()) {
                if (value!=null&&String.valueOf(value).matches("\\d+")){
                    return Integer.parseInt(String.valueOf(value));
                }
            }
        }
        return null;
    }

}
