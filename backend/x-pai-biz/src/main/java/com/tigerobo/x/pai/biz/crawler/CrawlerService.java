package com.tigerobo.x.pai.biz.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.crawler.CrawlerBlog;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CrawlerService {
    @Value("${pai.blog.crawler.block}")
    private String tranUri;// = "http://10.0.19.103:8002/export/v1/qs";
    @Value("${pai.blog.crawler.list}")
    private String listUri;// = "http://10.0.19.103:8002/export/v1/roll";


    public CrawlerBlog getCrawlerData(int offset) {

        Map<String, Object> params = new HashMap<>();

        params.put("type", "news.aiblog.930");
        params.put("app", "c01");
        params.put("sign", "e1fe4fc470b694994eba0f691c56a033");
        params.put("offset", offset);
        params.put("size", 100);
        String response = RestUtil.get(listUri, params);

        if (StringUtils.isEmpty(response)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(response);

        CrawlerBlog data = jsonObject.getObject("data", CrawlerBlog.class);

        return data;
    }

    public List<String> getBlogContent(Integer thirdId) {
        if (thirdId == null || thirdId == 0) {
            return null;
        }

        List<CrawlerContent> translateData = getTranslateData(thirdId);
        if (CollectionUtils.isEmpty(translateData)) {
            return null;
        }

        List<String> enList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(translateData)) {
            for (int i = 0; i < translateData.size(); i++) {
                CrawlerContent item = translateData.get(i);
                String section_content = item.getSection_content();
                if (StringUtils.isEmpty(section_content)) {
                    continue;
                }
                enList.add(section_content);
            }
        }
        return enList;
    }

    private List<CrawlerContent> getTranslateData(int thirdId) {

        Map<String, Object> params = new HashMap<>();
        params.put("type", "news.aiblog.content.931");
        params.put("app", "c01");
        params.put("sign", "e1fe4fc470b694994eba0f691c56a033");
        params.put("wd", thirdId);
        String response = RestUtil.get(tranUri, params);

        if (StringUtils.isEmpty(response)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(response);
        JSONArray data = jsonObject.getJSONArray("data");

        return JSON.parseArray(JSON.toJSONString(data), CrawlerContent.class);
    }


    @Data
    public static class CrawlerContent {
        Integer section_order;
        String section_content;
        String section_trans;
    }

}
