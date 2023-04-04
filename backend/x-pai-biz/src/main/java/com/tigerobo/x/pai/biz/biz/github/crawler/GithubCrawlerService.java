package com.tigerobo.x.pai.biz.biz.github.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class GithubCrawlerService {


    String host = "http://10.0.19.103:11800/";
    String remoteUrl = host+ "rest/api/v1/rem/submit?app=c01&sign=e1fe4fc470b694994eba0f691c56a033";

    public Integer githubUrlReq(String url){
        Validate.isTrue(url!=null&&url.startsWith("http"),"url格式不正确");
        Map<String,Object> data = new LinkedHashMap<>();

        data.put("type","github");
        data.put("list", Arrays.asList(url));

        final String post = RestUtil.post(remoteUrl, data);

        Validate.isTrue(StringUtils.isNotBlank(post),"爬虫未响应");

        final JSONObject jsonObject = JSON.parseObject(post);

        final Integer code = jsonObject.getInteger("code");
        final String msg = jsonObject.getString("msg");
        Validate.isTrue(code!=null&&code.equals(0),msg);

        final JSONObject exist = jsonObject.getJSONObject("data").getJSONObject("exist");

        if (exist!=null&&!exist.isEmpty()){
            return Integer.parseInt((new ArrayList(exist.values())).get(0).toString());
        }
        return null;
    }

    public static void main(String[] args) {
        String url = "https://github.com/open-mmlab";
        GithubCrawlerService crawlerService = new GithubCrawlerService();

        final Integer id = crawlerService.githubUrlReq(url);
        System.out.println(id);
    }
}
